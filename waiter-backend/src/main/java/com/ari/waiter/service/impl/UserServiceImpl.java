package com.ari.waiter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.enums.UserRoleEnum;
import com.ari.waiter.common.model.enums.UserStatusEnum;
import com.ari.waiter.common.model.vo.UserVo;
import com.ari.waiter.common.request.IdRequest;
import com.ari.waiter.common.response.BaseResponse;
import com.ari.waiter.common.response.PageResponse;
import com.ari.waiter.common.response.ResultUtils;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.utils.SqlUtils;
import com.ari.waiter.common.validator.UserValidator;
import com.ari.waiter.model.dto.user.*;
import com.ari.waiter.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ari.waiter.common.model.entity.User;
import com.ari.waiter.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.ari.waiter.common.constant.CommonConstant.SORT_ORDER_ASC;
import static com.ari.waiter.common.constant.CommonConstant.WAITER_SALT;
import static com.ari.waiter.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author ari24charles
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-03-06 14:25:03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public BaseResponse<UserVo> userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String check = userRegisterRequest.getCheck();
        String phone = userRegisterRequest.getPhone();
        // 1. 校验
        if (StrUtil.hasBlank(username, password, check, phone)) { // 非空校验
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数为空");
        }
        UserValidator.validUsername(username); // 账号校验
        UserValidator.validPassword(password); // 密码校验
        if (!password.equals(check)) { // 密码应该和校验密码相同
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码应该和校验密码相同");
        }
        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (count > 0) { // 手机号不能重复
            throw new BusinessException(StatusCode.PARAMS_ERROR, "该手机号已经被注册了");
        }
        count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0) { // 账号不能重复
            throw new BusinessException(StatusCode.PARAMS_ERROR, "该账号已存在");
        }
        // 2. 字段处理
        String encryptPassword = DigestUtil.md5Hex(WAITER_SALT + password); // 密码加密
        String randomNickname = IdUtil.simpleUUID().substring(0, 16); // 随机生成用户昵称
        // 3. 插入数据
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setPhone(phone);
        user.setNickname(randomNickname);
        // 设置 ak 和 sk
        String accessKey = DigestUtil.md5Hex(WAITER_SALT + username + RandomUtil.randomNumbers(4));
        String secretKey = DigestUtil.md5Hex(WAITER_SALT + username + RandomUtil.randomNumbers(8));
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean isSuccess = this.save(user);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        // 4. 获取新用户视图
        User newUser = this.getById(user.getId());
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(newUser, userVo);
        // 5. 脱敏
        UserVo desensitizedUserVo = desensitize(userVo);
        // 6. 记录用户登陆态
        request.getSession().setAttribute(USER_LOGIN_STATE, desensitizedUserVo);
        return ResultUtils.success(userVo);
    }

    @Override
    public BaseResponse<UserVo> userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();
        // 1. 校验
        if (StrUtil.hasBlank(username, password)) { // 非空校验
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数为空");
        }
        UserValidator.validUsername(username); // 账号校验
        UserValidator.validPassword(password); // 密码校验
        // 2. 获取加密后的密码
        String encryptPassword = DigestUtil.md5Hex(WAITER_SALT + password);
        // 3. 查询数据库
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getPassword, encryptPassword));
        if (user == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 校验用户是否封禁
        if (user.getStatus().equals(UserStatusEnum.BANNED.getValue())) {
            throw new BusinessException(StatusCode.BANNED_ERROR);
        }
        // 5. 获取用户视图
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        // 6. 脱敏
        UserVo desensitizedUserVo = desensitize(userVo);
        // 7. 记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, desensitizedUserVo);
        return ResultUtils.success(userVo);
    }

    @Override
    public BaseResponse<?> userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<UserVo> getCurrentUser(HttpServletRequest request) {
        // 获取当前用户
        User user = getLatestUser(request);
        // 获取用户脱敏视图
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        UserVo desensitizedUserVo = desensitize(userVo);
        // 更新用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, desensitizedUserVo);
        return ResultUtils.success(userVo);
    }

    @Override
    public BaseResponse<UserVo> updateUser(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 1. 获取当前用户
        User user = getLatestUser(request);
        // 2. 获取请求中的参数
        String username = userUpdateRequest.getUsername();
        String password = userUpdateRequest.getPassword();
        String nickname = userUpdateRequest.getNickname();
        String phone = userUpdateRequest.getPhone();
        String email = userUpdateRequest.getEmail();
        String avatarUrl = userUpdateRequest.getAvatarUrl();
        Integer gender = userUpdateRequest.getGender();
        // 3. 校验参数
        if (StrUtil.isAllBlank(username, password, nickname, phone, email, avatarUrl)) {
            if (gender == null) { // 所有参数为空
                throw new BusinessException(StatusCode.PARAMS_ERROR, "所有参数都为空");
            }
        }
        if (gender != null) {
            UserValidator.validGender(gender);
        }
        if (password != null) {
            UserValidator.validPassword(password);
        }
        // 账号不能重复
        if (username != null && !username.equals(user.getUsername())) {
            UserValidator.validUsername(username);
            long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "该账号已存在");
            }
        }
        if (phone != null && !phone.equals(user.getPhone())) {
            // 手机号不能重复
            long count = this.count(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
            if (count > 0) {
                throw new BusinessException(StatusCode.PARAMS_ERROR, "该手机号已经被注册了");
            }
        }
        // 4. 获取加密后的密码
        String encryptPassword = DigestUtil.md5Hex(WAITER_SALT + password);
        // 5. 动态更新
        boolean isSuccess = this.update(new LambdaUpdateWrapper<User>()
                .set(username != null, User::getUsername, username)
                .set(password != null, User::getPassword, encryptPassword)
                .set(nickname != null, User::getNickname, nickname)
                .set(phone != null, User::getPhone, phone)
                .set(email != null, User::getEmail, email)
                .set(avatarUrl != null, User::getAvatarUrl, avatarUrl)
                .set(gender != null, User::getGender, gender)
                .eq(User::getId, user.getId()));
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        // 6. 得到该用户的最新个人信息并脱敏
        User updatedUser = this.getById(user.getId());
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(updatedUser, userVo);
        UserVo desensitizedUserVo = desensitize(userVo);
        // 7. 将脱敏视图存于登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, desensitizedUserVo);
        return ResultUtils.success(userVo);
    }

    @Override
    public BaseResponse<?> deleteUser(IdRequest idRequest, HttpServletRequest request) {
        Long userId = idRequest.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数错误");
        }
        // 1. 获取当前用户
        User currentUser = getLatestUser(request);
        // 2. 如果当前用户 id 与传入的 userId 不同，则没有注销他人账号的权限
        if (!currentUser.getId().equals(userId)) {
            throw new BusinessException(StatusCode.NO_AUTH, "注销失败");
        }
        boolean isSuccess = this.removeById(userId);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "用户已注销");
        }
        // 清除 Session
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<PageResponse<UserVo>> searchUser(UserQueryRequest userQueryRequest) {
        // 1. 获取参数
        String username = userQueryRequest.getUsername();
        String nickname = userQueryRequest.getNickname();
        Integer currentPage = userQueryRequest.getCurrent();
        Integer pageSize = userQueryRequest.getPageSize();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        // 2. 动态查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(username), "username", username)
                .or(StrUtil.isNotBlank(nickname)).like(StrUtil.isNotBlank(nickname), "nickname", nickname)
                .eq("status", 0)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        // 3. 分页查询
        IPage<User> page = new Page<>(currentPage, pageSize);
        IPage<User> userPage = this.page(page, queryWrapper);
        // 4. 组装返回结果
        PageResponse<UserVo> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(userPage.getCurrent());
        pageResponse.setSize(userPage.getSize());
        pageResponse.setTotal(userPage.getTotal());
        // 转换为用户视图并脱敏
        List<User> records = userPage.getRecords();
        List<UserVo> userVoList = new ArrayList<>();
        for (User user : records) {
            UserVo userVo = new UserVo();
            BeanUtil.copyProperties(user, userVo);
            UserVo desensitizedUserVo = desensitize(userVo);
            userVoList.add(desensitizedUserVo);
        }
        pageResponse.setRecords(userVoList);
        return ResultUtils.success(pageResponse);
    }

    @Override
    public BaseResponse<PageResponse<User>> listUser(UserQueryAdminRequest userQueryAdminRequest) {
        // 1. 获取参数
        String username = userQueryAdminRequest.getUsername();
        String nickname = userQueryAdminRequest.getNickname();
        Integer gender = userQueryAdminRequest.getGender();
        Integer status = userQueryAdminRequest.getStatus();
        Integer role = userQueryAdminRequest.getRole();
        Integer currentPage = userQueryAdminRequest.getCurrent();
        Integer pageSize = userQueryAdminRequest.getPageSize();
        String sortField = userQueryAdminRequest.getSortField();
        String sortOrder = userQueryAdminRequest.getSortOrder();
        // 2. 动态查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(username), "username", username)
                .like(StrUtil.isNotBlank(nickname), "nickname", nickname)
                .eq(gender != null, "gender", gender)
                .eq(status != null, "status", status)
                .eq(role != null, "role", role)
                .orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(SORT_ORDER_ASC), sortField);
        // 3. 分页查询
        IPage<User> page = new Page<>(currentPage, pageSize);
        IPage<User> userPage = this.page(page, queryWrapper);
        // 4. 组装返回结果
        PageResponse<User> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(userPage.getCurrent());
        pageResponse.setSize(userPage.getSize());
        pageResponse.setTotal(userPage.getTotal());
        // 脱敏
        List<User> records = userPage.getRecords();
        pageResponse.setRecords(desensitizeBatch(records));
        return ResultUtils.success(pageResponse);
    }

    @Override
    public BaseResponse<?> switchUserRole(IdRequest idRequest) {
        Long userId = idRequest.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数错误");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在");
        }
        if (user.getRole().equals(UserRoleEnum.ADMIN.getValue())) {
            updateUser.setRole(UserRoleEnum.USER.getValue());
        } else {
            if (user.getRole().equals(UserRoleEnum.USER.getValue())) {
                updateUser.setRole(UserRoleEnum.ADMIN.getValue());
            } else {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "用户角色异常");
            }
        }
        boolean isSuccess = this.updateById(updateUser);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<?> switchUserStatus(IdRequest idRequest) {
        Long userId = idRequest.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "参数错误");
        }
        User updateUser = new User();
        updateUser.setId(userId);
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在");
        }
        if (user.getStatus().equals(UserStatusEnum.NORMAL.getValue())) {
            updateUser.setStatus(UserStatusEnum.BANNED.getValue());
        } else {
            if (user.getStatus().equals(UserStatusEnum.BANNED.getValue())) {
                updateUser.setStatus(UserStatusEnum.NORMAL.getValue());
            } else {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "用户状态异常");
            }
        }
        boolean isSuccess = this.updateById(updateUser);
        if (!isSuccess) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return ResultUtils.success();
    }

    @Override
    public UserVo desensitize(UserVo userVo) {
        if (userVo == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在");
        }
        userVo.setPhone(DesensitizedUtil.mobilePhone(userVo.getPhone())); // 手机号脱敏
        String email = userVo.getEmail();
        if (!StrUtil.isBlank(email)) { // 邮箱脱敏
            userVo.setEmail(StrUtil.hide(email, email.length() / 4, (int) (email.length() * 0.75)));
        }
        String secretKey = userVo.getSecretKey(); // 签名密钥脱敏
        userVo.setSecretKey(StrUtil.hide(secretKey, secretKey.length() / 4, (int) (secretKey.length() * 0.75)));
        return userVo;
    }

    @Override
    public List<User> desensitizeBatch(List<User> userList) {
        if (userList == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户不存在");
        }
        for (User user : userList) {
            user.setPassword(null); // 剔除密码
            user.setPhone(DesensitizedUtil.mobilePhone(user.getPhone())); // 手机号脱敏
            String email = user.getEmail();
            if (!StrUtil.isBlank(email)) { // 邮箱脱敏
                user.setEmail(StrUtil.hide(email, email.length() / 4, (int) (email.length() * 0.75)));
            }
            user.setAccessKey(null); // 剔除密钥
            user.setSecretKey(null); // 剔除签名密钥
            user.setIsDeleted(null); // 剔除逻辑删除
        }
        return userList;
    }

    @Override
    public User getLatestUser(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVo userVo = (UserVo) object;
        // 存在 Session 中的用户信息不一定是最新的
        User user = this.getById(userVo.getId());
        if (user == null) {
            request.getSession().removeAttribute(USER_LOGIN_STATE);
            throw new BusinessException(StatusCode.PARAMS_ERROR, "用户已注销");
        }
        if (user.getStatus().equals(UserStatusEnum.BANNED.getValue())) {
            request.getSession().removeAttribute(USER_LOGIN_STATE);
            throw new BusinessException(StatusCode.BANNED_ERROR);
        }
        return user;
    }
}
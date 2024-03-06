package com.ari.waiter.common.validator;

import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.enums.UserGenderEnum;
import com.ari.waiter.common.model.vo.UserVo;
import com.ari.waiter.common.response.StatusCode;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ari.waiter.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户实体字段校验类
 *
 * @author ari24charles
 */
public class UserValidator {

    /**
     * 获取当前登陆用户 id
     *
     * @param request 请求
     * @return 用户 id
     */
    public static Long validUserLoginState(HttpServletRequest request) {
        UserVo loginUserVo = (UserVo) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (loginUserVo == null) {
            throw new BusinessException(StatusCode.NOT_LOGIN);
        }
        return loginUserVo.getId();
    }

    /**
     * 校验用户的账号字段
     *
     * @param username 账号
     */
    public static void validUsername(String username) {
        // 账号的长度应该不小于4位
        if (username.length() < 4) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号应该不小于4位");
        }
        // 账号的长度应该小于30位
        if (username.length() > 30) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号应该小于30位");
        }
        // 账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(username);
        if (matcher.find()) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
    }

    /**
     * 校验用户的密码字段
     *
     * @param password 密码
     */
    public static void validPassword(String password) {
        // 密码应该不小于8位
        if (password.length() < 8) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码应该不小于8位");
        }
        // 密码应该小于50位
        if (password.length() > 50) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "密码应该小于50位");
        }
    }

    /**
     * 校验用户的密码字段
     *
     * @param gender 性别
     */
    public static void validGender(Integer gender) {
        if (!(gender.equals(UserGenderEnum.UNKNOWN.getValue()) ||
                gender.equals(UserGenderEnum.MAlE.getValue()) ||
                gender.equals(UserGenderEnum.FEMALE.getValue()))) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "性别异常");
        }
    }
}

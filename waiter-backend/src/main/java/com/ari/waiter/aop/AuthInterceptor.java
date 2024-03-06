package com.ari.waiter.aop;

import cn.hutool.core.util.StrUtil;
import com.ari.waiter.common.annotation.AuthCheck;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.model.entity.User;
import com.ari.waiter.common.model.enums.UserRoleEnum;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.validator.UserValidator;
import com.ari.waiter.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户权限校验
 *
 * @author ari24charles
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 拦截所有添加了 @AuthCheck 注解的方法，进行鉴权操作
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 校验用户登录状态
        Long userId = UserValidator.validUserLoginState(request);
        User loginUser = userService.getById(userId);
        if (loginUser == null) { // 用户不存在
            throw new BusinessException(StatusCode.NO_USER);
        }
        // 获取方法所需的权限列表
        String[] mustRoles = authCheck.mustRole();
        if (mustRoles == null || mustRoles.length == 0) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        // 鉴权，多个具有权限的角色中有一个角色即可放行
        for (String role : mustRoles) {
            if (StrUtil.isNotBlank(role)) { // 要求的角色非空
                if (loginUser.getRole().equals(UserRoleEnum.getValueByText(role))) {
                    return joinPoint.proceed(); // 登录用户的角色与要求的角色相同
                }
            }
        }
        // 所有角色都不满足，则通不过权限校验
        throw new BusinessException(StatusCode.NO_AUTH);
    }
}

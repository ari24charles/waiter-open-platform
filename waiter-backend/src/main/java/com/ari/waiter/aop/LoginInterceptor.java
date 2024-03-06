package com.ari.waiter.aop;

import com.ari.waiter.common.annotation.LoginCheck;
import com.ari.waiter.common.exception.BusinessException;
import com.ari.waiter.common.response.StatusCode;
import com.ari.waiter.common.validator.UserValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录校验
 *
 * @author ari24charles
 */
@Aspect
@Component
public class LoginInterceptor {

    /**
     * 拦截所有添加了 @LoginCheck 注解的方法，进行登录校验
     */
    @Around("@annotation(loginCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 校验用户登录状态
        Long userId = UserValidator.validUserLoginState(request);
        if (userId == null || userId <= 0 ) {
            throw new BusinessException(StatusCode.NOT_LOGIN);
        }
        // 通过登录校验
        return joinPoint.proceed();
    }
}

package org.com.stocknote.global.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import java.lang.reflect.Parameter;


@Aspect
@Component
@Order(1)
public class EmailAspect {

    @Around("@annotation(InjectEmail)")
    public Object injectEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SecurityException("Authentication not found");
        }

        String userEmail = authentication.getName();

        // 파라미터 이름을 기반으로 이메일 주입
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType() == String.class &&
                    (parameter.getName().toLowerCase().contains("email") ||
                            parameter.getName().toLowerCase().contains("username"))) {
                args[i] = userEmail;
            }
        }

        return joinPoint.proceed(args);
    }
}

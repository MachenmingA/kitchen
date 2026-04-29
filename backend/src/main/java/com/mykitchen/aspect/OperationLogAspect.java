package com.mykitchen.aspect;

import com.mykitchen.entity.OperationLog;
import com.mykitchen.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {
    
    private final OperationLogMapper operationLogMapper;
    
    @Pointcut("execution(* com.mykitchen.controller..*.*(..))")
    public void controllerPointcut() {}
    
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        try {
            HttpServletRequest request = getRequest();
            if (request != null) {
                OperationLog log = new OperationLog();
                log.setUserId(getUserIdFromRequest(request));
                log.setOperation(joinPoint.getSignature().getName());
                log.setMethod(request.getMethod());
                log.setUrl(request.getRequestURI());
                log.setIp(getClientIp(request));
                
                operationLogMapper.insert(log);
            }
        } catch (Exception e) {
            // Log error, don't affect main flow
        }
        
        return result;
    }
    
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            Object user = request.getAttribute("currentUser");
            if (user != null) {
                Method getId = user.getClass().getMethod("getId");
                return (Long) getId.invoke(user);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

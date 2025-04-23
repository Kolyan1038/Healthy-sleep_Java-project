package org.healthysleep.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.healthysleep.service.VisitCounterService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class VisitAspect {
    
    private final VisitCounterService visitCounterService;
    
    public VisitAspect(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }
    
    @Before("execution(* org.healthysleep.controller..*(..))")
    public void registerVisit() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        visitCounterService.registerVisit(uri);
    }
}
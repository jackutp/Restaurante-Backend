package com.microservicio.pagos.db_config;

import com.microservicio.pagos.db_config.DataSourceContextHolder;
import com.microservicio.pagos.db_config.DatabaseType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class ReadOnlyConnectionInterceptor {
    @Around("@annotation(transactional)")
    public Object route(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable{
        try{
            if(transactional.readOnly()){
                DataSourceContextHolder.set(DatabaseType.READ);
            } else {
                DataSourceContextHolder.set(DatabaseType.WRITE);
            }
            return joinPoint.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}

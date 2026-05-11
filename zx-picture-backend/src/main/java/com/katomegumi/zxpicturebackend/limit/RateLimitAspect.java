package com.katomegumi.zxpicturebackend.limit;

import com.katomegumi.zxpicturebackend.security.sa.StpKit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : lr
 * @description : 限流切面逻辑
 * @createDate : 2026/5/8 下午8:08
 */
@Aspect
@Component
public class RateLimitAspect {

    /**
     * redis限流执行器
     */
    @Resource
    private RedisRateLimiter redisRateLimiter;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        //获得http请求对象
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        String key = builderKey(httpServletRequest, rateLimit);
        boolean allowed = redisRateLimiter.isAllowed(key, rateLimit.limit(), rateLimit.window());
        if (!allowed) {
            throw new RuntimeException(rateLimit.message());
        }
        return point.proceed();
    }


    private String builderKey(HttpServletRequest request, RateLimit rateLimit) {
        String uri = request.getRequestURI();
        return switch (rateLimit.type()) {
            case IP -> rateLimit.key() + ":ip:" + request.getRemoteAddr() + uri;
            case USER -> rateLimit.key() + ":userId:" + StpKit.USER.getLoginIdAsString() + uri;
            default -> rateLimit.key() + ":api:" + uri;
        };
    }

}


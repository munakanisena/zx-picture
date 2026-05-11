package com.katomegumi.zxpicturebackend.security.aspect;

import com.katomegumi.zxpicturebackend.common.exception.BusinessException;
import com.katomegumi.zxpicturebackend.common.exception.ErrorCode;
import com.katomegumi.zxpicturebackend.entity.UserInfo;
import com.katomegumi.zxpicturebackend.enums.UserRoleEnum;
import com.katomegumi.zxpicturebackend.manager.cache.UserCacheManager;
import com.katomegumi.zxpicturebackend.security.annotation.AuthCheck;
import com.katomegumi.zxpicturebackend.security.sa.StpKit;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 通过springboot-aop切面 加上注解 进行身份校验
 *
 * @author lr
 */
@Component
@Aspect
@RequiredArgsConstructor
public class AuthCheckAspect {

    private final UserCacheManager userCacheManager;

    /**
     * 进行用户还是管理员权限效验
     *
     * @param joinPoint
     * @param authCheck
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doAround(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        if (authCheck.mustLogin()) {
            if (!StpKit.USER.isLogin()) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
            }
        }
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByKey(mustRole);
        //如果为空 说明无需权限直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        //获取当前用户
        UserInfo userInfo = userCacheManager.getUserInfoCache(StpKit.USER.getLoginIdAsLong());
        //获取用户的枚举常量
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByKey(userInfo.getRole());
        // 要求必须有管理员权限
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}

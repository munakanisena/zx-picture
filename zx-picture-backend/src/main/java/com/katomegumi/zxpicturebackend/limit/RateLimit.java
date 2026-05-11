package com.katomegumi.zxpicturebackend.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : lr
 * @description : 限流注解 QPS= window * limit
 * @createDate : 2026/5/8 下午7:37
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流 key 前缀
     */
    String key() default "rate_limit";

    /**
     * 时间窗口大小（秒）
     */
    int window() default 60;


    /**
     * 时间窗口内允许的请求数
     */
    int limit() default 30;

    /**
     * 限流提示信息
     */
    String message() default "系统繁忙，请稍后再试";


    /**
     * 限流维度（默认按IP限流）
     */
    LimitType type() default LimitType.IP;

    enum LimitType {
        /**
         * 按调用方IP限流
         */
        IP,
        /**
         * 按用户ID限流
         */
        USER,
        /**
         * 按方法限流
         */
        API
    }
}

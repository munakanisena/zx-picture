package com.katomegumi.zxpicturebackend.common.exception;

/**
 * 异常工具类 断言类
 *
 * @author lr
 */
public class ThrowUtils {
    /**
     * 条件成立抛异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}

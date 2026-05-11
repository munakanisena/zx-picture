package com.katomegumi.zxpicturebackend.common.constant;

import lombok.experimental.UtilityClass;

/**
 *
 * @author lr
 * @description user常量类
 */
@UtilityClass
public class UserConstant {
    //  region 权限
    /**
     * 默认角色
     */
    public static final String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    public static final String ADMIN_ROLE = "admin";

    // endregion

    // 防止缓存穿透 设置默认值
    public static final String EMPTY_USER = "empty_user";

    //加盐值
    public static final String SALT = "Megumi";
}

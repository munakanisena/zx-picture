package com.katomegumi.zxpicturebackend.common.constant;

import lombok.experimental.UtilityClass;

/**
 * @author : lr
 * @description : 图片常量
 * @createDate : 2025/5/25 下午4:52
 */
@UtilityClass
public class PictureConstant {

    /**
     * cos存储路径图片前缀
     */
    public static final String PICTURE_PREFIX = "images/";

    /**
     * 公共图片存储路径前缀
     */
    public static final String PUBLIC_PICTURE_PREFIX = PICTURE_PREFIX + "public/";

    /**
     * 空间图片存储路径前缀
     */
    public static final String SPACE_PICTURE_PREFIX = PICTURE_PREFIX + "space/";

    /**
     * AI扩图图片存储路径前缀
     */
    public static final String CAPTURE_PICTURE_PREFIX = PICTURE_PREFIX + "capture/";

    /**
     * 图片分类 顶层父分类 id默认0
     */
    public static final long PICTURE_CATEGORY_ROOT_PARENT_ID = 0L;

    /**
     * websocket 会话参数
     */
    public static final String ATTR_PICTURE_ID = "pictureId";
}


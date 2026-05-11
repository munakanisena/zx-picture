package com.katomegumi.zxpicturebackend.security.model;

import lombok.Data;

/**
 * @author lr
 * @description 请求上下文对象
 */
@Data
public class SpaceUserAuthContext {

    /**
     * 空间 ID
     */
    private Long spaceId;
}

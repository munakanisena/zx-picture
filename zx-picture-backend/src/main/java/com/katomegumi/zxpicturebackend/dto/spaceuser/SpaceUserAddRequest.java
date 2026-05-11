package com.katomegumi.zxpicturebackend.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lr
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 空间 ID
     */
    private Long spaceId;
    /**
     * 添加的用户名
     */
    private String userName;
    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;
}

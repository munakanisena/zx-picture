package com.katomegumi.zxpicturebackend.dto.spaceuser;

import com.katomegumi.zxpicturebackend.common.req.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author lr
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;
}

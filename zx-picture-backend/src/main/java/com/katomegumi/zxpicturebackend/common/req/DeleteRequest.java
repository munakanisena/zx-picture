package com.katomegumi.zxpicturebackend.common.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lr
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 要删除的id
     */
    private Long id;

    /**
     * 空间 ID(校验团队空间时用到)
     */
    private Long spaceId;

}

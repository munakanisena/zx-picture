package com.katomegumi.zxpicturebackend.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : lr
 * @description : 图片互动请求类
 * @createDate : 2025/6/1 下午4:16
 */
@Data
public class PictureInteractionRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 图片 ID
     */
    private Long id;
    /**
     * 交互类型 0点赞 1收藏 2下载
     */
    private Integer interactionType;
    /**
     * 交互状态 0未互动 1已互动
     */
    private Integer interactionStatus;

}


package com.katomegumi.zxpicturebackend.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 管理员使用
 *
 * @author lr
 */
@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称（展示）
     */
    private String picName;

    /**
     * 图片描述（展示）
     */
    private String picDesc;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}

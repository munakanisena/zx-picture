package com.katomegumi.zxpicturebackend.vo.space.analyze;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 空间分析标签
 *
 * @author lr
 */
@Data
@Builder
public class SpaceTagAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签的图片数量
     */
    private Long count;
}

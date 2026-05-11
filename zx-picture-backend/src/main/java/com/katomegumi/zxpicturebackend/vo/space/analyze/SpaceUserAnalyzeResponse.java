package com.katomegumi.zxpicturebackend.vo.space.analyze;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 建造者模式 空间分析结果
 *
 * @author lr
 */
@Data
@Builder
public class SpaceUserAnalyzeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 时间区间
     */
    private String period;
    /**
     * 上传数量
     */
    private Long count;
}

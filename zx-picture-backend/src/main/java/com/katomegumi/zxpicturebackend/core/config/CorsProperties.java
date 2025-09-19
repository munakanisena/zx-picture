package com.katomegumi.zxpicturebackend.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : Megumi
 * @description : 跨域配置
 * @createDate : 2025/9/19 下午4:27
 */
@Data
@ConfigurationProperties(prefix = "cors")
@Component
public class CorsProperties {
    /**
     * 允许跨域的域名
     */
    private List<String> allowOrigins;
}


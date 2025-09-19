package com.katomegumi.zxpicturebackend.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Megumi
 * @description 上线采用 nginx 代理转发 允许跨域
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //允许发送cookie
                .allowCredentials(true)
                //放行哪些域名 因为allowCredentials要发送cookie 所以必须是具体的源地址
                .allowedOrigins(corsProperties.getAllowOrigins().toArray(new String[0]))
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .exposedHeaders("*");
    }
}

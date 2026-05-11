package com.katomegumi.zxpicturebackend;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author lr
 * @description 启动类
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@MapperScan("com.katomegumi.zxpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true) //暴露代理对象
@EnableCaching
public class ZxPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZxPictureBackendApplication.class, args);
    }

}

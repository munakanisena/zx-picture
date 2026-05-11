package com.katomegumi.zxpicturebackend.limit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * @author : lr
 * @description : redis限流执行器
 * @createDate : 2026/5/8 下午7:50
 */
@Component
public class RedisRateLimiter {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> script;

    RedisRateLimiter(){
        this.script=new DefaultRedisScript<>();
        //设置返回类型
        this.script.setResultType(Long.class);
        //设置lua脚本路径
        this.script.setLocation(new ClassPathResource("/lua/rateLimit.lua"));
    }

    /**
     * 判断是否限流
     * @param key 限流key
     * @param limit 时间窗口内允许的请求数
     * @param window 时间窗口（单位: 秒）
     * @return true 放行 false:此时限流
     */
    public boolean isAllowed(String key,int limit,int window){
        //执行脚本
        Long result = stringRedisTemplate.execute(script,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(window));
        return result!=null && result==1;
    }
}


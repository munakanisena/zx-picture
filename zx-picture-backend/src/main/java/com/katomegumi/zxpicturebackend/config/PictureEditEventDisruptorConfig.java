package com.katomegumi.zxpicturebackend.config;


import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.katomegumi.zxpicturebackend.manager.websocket.disruptor.PictureEditEvent;
import com.katomegumi.zxpicturebackend.manager.websocket.disruptor.PictureEditEventWorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author : lr
 * @description : 队列配置
 */
@Configuration
@RequiredArgsConstructor
public class PictureEditEventDisruptorConfig {

    private final PictureEditEventWorkHandler pictureEditEventWorkHandler;

    @Bean("pictureEditEventDisruptor")
    public Disruptor<PictureEditEvent> messageModelRingBuffer() {
        //设置队列空间
        // ringBuffer 的大小
        int bufferSize = 1024 * 256;
        Disruptor<PictureEditEvent> disruptor = new Disruptor<>(PictureEditEvent::new, bufferSize, ThreadFactoryBuilder.create().setNamePrefix("pictureEditEventDisruptor").build());
        //设置消费者
        disruptor.handleEventsWithWorkerPool(pictureEditEventWorkHandler);
        //开启队列
        disruptor.start();
        return disruptor;
    }

}

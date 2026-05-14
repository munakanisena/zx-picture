package com.katomegumi.zxpicturebackend.manager.websocket.disruptor;

import com.katomegumi.zxpicturebackend.entity.UserInfo;
import com.katomegumi.zxpicturebackend.manager.websocket.model.PictureEditRequestMessage;
import com.katomegumi.zxpicturebackend.manager.websocket.model.enums.PictureEditMessageTypeEnum;
import com.katomegumi.zxpicturebackend.manager.websocket.strategy.PictureEditMessageStrategy;
import com.katomegumi.zxpicturebackend.manager.websocket.strategy.PictureEditMessageStrategyFactory;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 消费者  WorkHandler<PictureEditEvent> 泛型是自定义的消息事件
 *
 * @author lr
 */
@Slf4j
@Component
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {
    @Resource
    private PictureEditMessageStrategyFactory pictureEditMessageStrategyFactory;

    @Override
    public void onEvent(PictureEditEvent event) throws Exception {
        PictureEditRequestMessage pictureEditRequestMessage = event.getPictureEditRequestMessage();
        WebSocketSession session = event.getSession();
        UserInfo userInfo = event.getUser();
        Long pictureId = event.getPictureId();
        // 获取到消息类别
        String type = pictureEditRequestMessage.getType();
        PictureEditMessageStrategy strategy = pictureEditMessageStrategyFactory.getStrategy(type);
        strategy.handle(pictureEditRequestMessage, pictureId, session, userInfo);
    }
}

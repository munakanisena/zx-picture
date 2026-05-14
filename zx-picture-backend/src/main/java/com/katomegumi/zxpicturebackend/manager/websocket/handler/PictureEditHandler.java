package com.katomegumi.zxpicturebackend.manager.websocket.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.katomegumi.zxpicturebackend.entity.UserInfo;
import com.katomegumi.zxpicturebackend.manager.websocket.disruptor.PictureEditEventProducer;
import com.katomegumi.zxpicturebackend.manager.websocket.model.PictureEditRequestMessage;
import com.katomegumi.zxpicturebackend.manager.websocket.model.PictureEditResponseMessage;
import com.katomegumi.zxpicturebackend.manager.websocket.model.enums.PictureEditMessageTypeEnum;
import com.katomegumi.zxpicturebackend.manager.websocket.strategy.PictureEditMessageStrategyFactory;
import com.katomegumi.zxpicturebackend.manager.websocket.util.PictureEditBroadcaster;
import com.katomegumi.zxpicturebackend.vo.user.UserDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.katomegumi.zxpicturebackend.common.constant.PictureConstant.ATTR_PICTURE_ID;
import static com.katomegumi.zxpicturebackend.common.constant.UserConstant.ATTR_USER_INFO;

/**
 * @author lr
 * @description websocket  文本消息处理器
 */
@Component
@RequiredArgsConstructor
public class PictureEditHandler extends TextWebSocketHandler {

    private final PictureEditMessageStrategyFactory pictureEditMessageStrategyFactory;

    private final PictureEditBroadcaster pictureEditBroadcaster;

    /**
     * 消息队列生产者
     */
    private final PictureEditEventProducer pictureEditEventProducer;

    /**
     * 首次初次连接成功时
     *
     * @param session 当前会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //写入会话
        Long pictureId = (Long) session.getAttributes().get(ATTR_PICTURE_ID);
        UserInfo userInfo = (UserInfo) session.getAttributes().get(ATTR_USER_INFO);
        //写入当前图片 会话集合
        pictureEditBroadcaster.addSession(pictureId, session);
        //构造响应
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        String message = String.format("%s用户加入了编辑", userInfo.getName());
        pictureEditResponseMessage.setUserDetailVO(BeanUtil.copyProperties(userInfo, UserDetailVO.class));
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getKey());
        pictureEditResponseMessage.setMessage(message);
        pictureEditBroadcaster.broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

    /**
     * 取消连接时触发
     *
     * @param session 当前会话
     * @param status  关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long pictureId = (Long) session.getAttributes().get(ATTR_PICTURE_ID);
        UserInfo userInfo = (UserInfo) session.getAttributes().get(ATTR_USER_INFO);
        //执行退出策略
        pictureEditMessageStrategyFactory.getStrategy(PictureEditMessageTypeEnum.EXIT_EDIT.getKey()).handle(null, pictureId, session, userInfo);
        //移除用户的编辑状态
        pictureEditBroadcaster.removeSession(pictureId, session);
        //构造响应
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getKey());
        String message = String.format("%s离开编辑", userInfo.getName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setUserDetailVO(BeanUtil.copyProperties(userInfo, UserDetailVO.class));
        pictureEditBroadcaster.broadcastToPicture(pictureId, pictureEditResponseMessage);
    }

    /**
     * 接收消息后 触发
     *
     * @param session 当前会话信息
     * @param message 客户端传来的消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long pictureId = (Long) session.getAttributes().get(ATTR_PICTURE_ID);
        UserInfo userInfo = (UserInfo) session.getAttributes().get(ATTR_USER_INFO);
        PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        pictureEditEventProducer.publishEvent(pictureEditRequestMessage, session, userInfo, pictureId);
    }
}

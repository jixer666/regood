package com.abc.business.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.abc.business.domain.dto.ChatMessageDTO;
import com.abc.business.domain.vo.ChatMessageVO;
import com.abc.business.service.ChatMessageService;
import com.abc.common.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private ChatMessageService chatMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("用户 {} 连接成功，当前在线人数：{}", userId, userSessions.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject json = JSONUtil.parseObj(payload);

        String type = json.getStr("type");

        if ("message".equals(type)) {
            Long senderId = getUserIdFromSession(session);
            if (senderId == null) {
                return;
            }

            ChatMessageDTO messageDTO = new ChatMessageDTO();
            messageDTO.setReceiverId(json.getLong("receiverId"));
            messageDTO.setContent(json.getStr("content"));
            messageDTO.setMessageType(json.getInt("messageType"));

            ChatMessageVO messageVO = chatMessageService.sendMessage(messageDTO, senderId);

            sendMessageToUser(messageDTO.getReceiverId(), JSONUtil.toJsonStr(messageVO));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("用户 {} 断开连接，当前在线人数：{}", userId, userSessions.size());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            log.error("用户 {} 连接发生错误：{}", userId, exception.getMessage());
            userSessions.remove(userId);
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1];
            try {
                return SecurityUtils.getUserIdFromToken(token);
            } catch (Exception e) {
                log.error("解析token失败：{}", e.getMessage());
            }
        }
        return null;
    }

    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败：{}", userId, e.getMessage());
            }
        }
    }

    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

}

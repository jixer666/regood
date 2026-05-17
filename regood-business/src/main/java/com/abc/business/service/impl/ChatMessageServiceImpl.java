package com.abc.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.abc.business.domain.dto.ChatMessageDTO;
import com.abc.business.domain.entity.ChatConversation;
import com.abc.business.domain.entity.ChatMessage;
import com.abc.business.domain.vo.ChatMessageVO;
import com.abc.business.mapper.ChatConversationMapper;
import com.abc.business.mapper.ChatMessageMapper;
import com.abc.business.service.ChatConversationService;
import com.abc.business.service.ChatMessageService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.domain.entity.User;
import com.abc.common.util.IdUtils;
import com.abc.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ChatMessageServiceImpl extends BaseServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper messageMapper;

    @Autowired
    private ChatConversationMapper conversationMapper;

    @Autowired
    private ChatConversationService conversationService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ChatMessageVO> getMessageList(Long conversationId, Long userId) {
        return messageMapper.selectMessageList(conversationId, userId);
    }

    @Override
    public List<ChatMessageVO> getLatestMessages(Long conversationId, Long userId) {
        return messageMapper.selectLatestMessages(conversationId, userId);
    }

    @Override
    @Transactional
    public ChatMessageVO sendMessage(ChatMessageDTO messageDTO, Long senderId) {
        Long receiverId = messageDTO.getReceiverId();

        ChatConversation conversation = conversationService.getOrCreateConversation(senderId, receiverId);

        ChatMessage message = new ChatMessage();
        message.setMessageId(IdUtils.getId());
        message.setConversationId(conversation.getConversationId());
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(messageDTO.getContent());
        message.setMessageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : 1);
        message.setIsRead(0);
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        message.setStatus(1);
        message.setVer(1);
        messageMapper.insert(message);

        conversation.setLastMessage(messageDTO.getContent());
        conversation.setLastMessageTime(new Date());
        conversation.setUpdateTime(new Date());

        if (conversation.getUser1Id().equals(senderId)) {
            conversation.setUnreadCount2(conversation.getUnreadCount2() + 1);
        } else {
            conversation.setUnreadCount1(conversation.getUnreadCount1() + 1);
        }
        conversationMapper.updateById(conversation);

        User sender = userMapper.selectById(senderId);
        User receiver = userMapper.selectById(receiverId);

        ChatMessageVO vo = BeanUtil.copyProperties(message, ChatMessageVO.class);
        if (sender != null) {
            vo.setSenderName(sender.getNickname());
            vo.setSenderAvatar(sender.getAvatar());
        }
        if (receiver != null) {
            vo.setReceiverName(receiver.getNickname());
            vo.setReceiverAvatar(receiver.getAvatar());
        }
        return vo;
    }

    @Override
    @Transactional
    public void markAsRead(Long conversationId, Long userId) {
        messageMapper.markAsRead(conversationId, userId);

        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            if (conversation.getUser1Id().equals(userId)) {
                conversation.setUnreadCount1(0);
            } else {
                conversation.setUnreadCount2(0);
            }
            conversation.setUpdateTime(new Date());
            conversationMapper.updateById(conversation);
        }
    }

}

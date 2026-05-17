package com.abc.business.service.impl;

import com.abc.business.domain.entity.ChatConversation;
import com.abc.business.domain.vo.ChatConversationVO;
import com.abc.business.mapper.ChatConversationMapper;
import com.abc.business.service.ChatConversationService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ChatConversationServiceImpl extends BaseServiceImpl<ChatConversationMapper, ChatConversation> implements ChatConversationService {

    @Autowired
    private ChatConversationMapper conversationMapper;

    @Override
    public List<ChatConversationVO> getConversationList(Long userId) {
        return conversationMapper.selectConversationList(userId);
    }

    @Override
    @Transactional
    public ChatConversation getOrCreateConversation(Long user1Id, Long user2Id) {
        Long minUserId = Math.min(user1Id, user2Id);
        Long maxUserId = Math.max(user1Id, user2Id);

        ChatConversation conversation = conversationMapper.selectByUserIds(minUserId, maxUserId);

        if (conversation == null) {
            conversation = new ChatConversation();
            conversation.setConversationId(IdUtils.getId());
            conversation.setUser1Id(minUserId);
            conversation.setUser2Id(maxUserId);
            conversation.setUnreadCount1(0);
            conversation.setUnreadCount2(0);
            conversation.setCreateTime(new Date());
            conversation.setUpdateTime(new Date());
            conversation.setStatus(1);
            conversation.setVer(1);
            conversationMapper.insert(conversation);
        }

        return conversation;
    }

}

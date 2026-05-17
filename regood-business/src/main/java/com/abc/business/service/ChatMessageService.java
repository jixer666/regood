package com.abc.business.service;

import com.abc.business.domain.dto.ChatMessageDTO;
import com.abc.business.domain.entity.ChatMessage;
import com.abc.business.domain.vo.ChatMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {

    List<ChatMessageVO> getMessageList(Long conversationId, Long userId);

    List<ChatMessageVO> getLatestMessages(Long conversationId, Long userId);

    ChatMessageVO sendMessage(ChatMessageDTO messageDTO, Long senderId);

    void markAsRead(Long conversationId, Long userId);

}

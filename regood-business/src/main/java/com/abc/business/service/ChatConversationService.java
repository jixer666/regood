package com.abc.business.service;

import com.abc.business.domain.dto.ChatMessageDTO;
import com.abc.business.domain.entity.ChatConversation;
import com.abc.business.domain.vo.ChatConversationVO;
import com.abc.business.domain.vo.ChatMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatConversationService extends IService<ChatConversation> {

    List<ChatConversationVO> getConversationList(Long userId);

    ChatConversation getOrCreateConversation(Long user1Id, Long user2Id);

}

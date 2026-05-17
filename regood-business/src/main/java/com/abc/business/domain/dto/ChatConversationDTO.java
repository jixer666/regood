package com.abc.business.domain.dto;

import lombok.Data;

@Data
public class ChatConversationDTO {

    private Long conversationId;

    private Long userId;

    private Integer pageNum;

    private Integer pageSize;

}

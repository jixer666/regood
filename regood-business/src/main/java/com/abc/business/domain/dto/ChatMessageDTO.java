package com.abc.business.domain.dto;

import lombok.Data;

@Data
public class ChatMessageDTO {

    private Long messageId;

    private Long conversationId;

    private Long receiverId;

    private String content;

    private Integer messageType;

    private Integer pageNum;

    private Integer pageSize;

}

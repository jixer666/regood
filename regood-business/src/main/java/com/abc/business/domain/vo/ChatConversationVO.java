package com.abc.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversationVO {

    private Long conversationId;

    private Long userId;

    private String username;

    private String nickname;

    private String avatar;

    private String lastMessage;

    private Date lastMessageTime;

    private Integer unreadCount;

}

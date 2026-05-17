package com.abc.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abc.common.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("tb_chat_conversation")
@AllArgsConstructor
@NoArgsConstructor
public class ChatConversation extends BaseEntity {

    @TableId
    private Long conversationId;

    private Long user1Id;

    private Long user2Id;

    private String lastMessage;

    private Date lastMessageTime;

    private Integer unreadCount1;

    private Integer unreadCount2;

}

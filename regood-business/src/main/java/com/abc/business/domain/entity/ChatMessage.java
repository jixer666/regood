package com.abc.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.abc.common.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("tb_chat_message")
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends BaseEntity {

    @TableId
    private Long messageId;

    private Long conversationId;

    private Long senderId;

    private Long receiverId;

    private String content;

    private Integer messageType;

    private Integer isRead;

}

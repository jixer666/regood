-- 聊天会话表
DROP TABLE IF EXISTS `tb_chat_conversation`;
CREATE TABLE `tb_chat_conversation` (
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `user1_id` bigint NOT NULL COMMENT '用户1ID（较小的用户ID）',
  `user2_id` bigint NOT NULL COMMENT '用户2ID（较大的用户ID）',
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息内容',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后一条消息时间',
  `unread_count1` int NULL DEFAULT 0 COMMENT '用户1未读消息数',
  `unread_count2` int NULL DEFAULT 0 COMMENT '用户2未读消息数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0停用）',
  `ver` int NULL DEFAULT 1 COMMENT '版本号',
  PRIMARY KEY (`conversation_id`) USING BTREE,
  UNIQUE INDEX `uk_users`(`user1_id` ASC, `user2_id` ASC) USING BTREE,
  INDEX `idx_user1_id`(`user1_id` ASC) USING BTREE,
  INDEX `idx_user2_id`(`user2_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天会话表' ROW_FORMAT = Dynamic;

-- 聊天消息表
DROP TABLE IF EXISTS `tb_chat_message`;
CREATE TABLE `tb_chat_message` (
  `message_id` bigint NOT NULL COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `message_type` int NULL DEFAULT 1 COMMENT '消息类型（1文本 2图片 3文件）',
  `is_read` int NULL DEFAULT 0 COMMENT '是否已读（0未读 1已读）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `status` int NULL DEFAULT 1 COMMENT '状态（1正常 0撤回）',
  `ver` int NULL DEFAULT 1 COMMENT '版本号',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

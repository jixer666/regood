package com.abc.business.controller;

import com.abc.business.domain.dto.ChatMessageDTO;
import com.abc.business.domain.vo.ChatConversationVO;
import com.abc.business.domain.vo.ChatMessageVO;
import com.abc.business.service.ChatConversationService;
import com.abc.business.service.ChatMessageService;
import com.abc.common.domain.vo.ApiResult;
import com.abc.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "聊天接口")
@RestController
@RequestMapping("/business/chat")
public class ChatController {

    @Autowired
    private ChatConversationService conversationService;

    @Autowired
    private ChatMessageService messageService;

    @ApiOperation("获取会话列表")
    @GetMapping("/conversations")
    public ApiResult<List<ChatConversationVO>> getConversationList() {
        Long userId = SecurityUtils.getUserId();
        List<ChatConversationVO> list = conversationService.getConversationList(userId);
        return ApiResult.success(list);
    }

    @ApiOperation("获取消息列表")
    @GetMapping("/messages/{conversationId}")
    public ApiResult<List<ChatMessageVO>> getMessageList(@PathVariable Long conversationId) {
        Long userId = SecurityUtils.getUserId();
        List<ChatMessageVO> list = messageService.getMessageList(conversationId, userId);
        return ApiResult.success(list);
    }

    @ApiOperation("发送消息")
    @PostMapping("/send")
    public ApiResult<ChatMessageVO> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        Long senderId = SecurityUtils.getUserId();
        ChatMessageVO messageVO = messageService.sendMessage(messageDTO, senderId);
        return ApiResult.success(messageVO);
    }

    @ApiOperation("标记消息已读")
    @PutMapping("/read/{conversationId}")
    public ApiResult<String> markAsRead(@PathVariable Long conversationId) {
        Long userId = SecurityUtils.getUserId();
        messageService.markAsRead(conversationId, userId);
        return ApiResult.success("已标记为已读");
    }

    @ApiOperation("获取最新消息")
    @GetMapping("/messages/{conversationId}/latest")
    public ApiResult<List<ChatMessageVO>> getLatestMessages(@PathVariable Long conversationId) {
        Long userId = SecurityUtils.getUserId();
        List<ChatMessageVO> list = messageService.getLatestMessages(conversationId, userId);
        return ApiResult.success(list);
    }

}

package com.abc.business.mapper;

import com.abc.business.domain.entity.ChatMessage;
import com.abc.business.domain.vo.ChatMessageVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    List<ChatMessageVO> selectMessageList(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    List<ChatMessageVO> selectLatestMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    void markAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

}

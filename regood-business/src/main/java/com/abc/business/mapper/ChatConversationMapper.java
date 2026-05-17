package com.abc.business.mapper;

import com.abc.business.domain.entity.ChatConversation;
import com.abc.business.domain.vo.ChatConversationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {

    ChatConversation selectByUserIds(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    List<ChatConversationVO> selectConversationList(@Param("userId") Long userId);

}

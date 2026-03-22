package com.abc.business.mapper;

import com.abc.business.domain.entity.Cart;
import com.abc.business.domain.dto.CartDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    List<Cart> selectCartList(CartDTO cartDTO);
}

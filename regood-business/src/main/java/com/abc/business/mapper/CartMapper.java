package com.abc.business.mapper;

import com.abc.business.domain.entity.Cart;
import com.abc.business.domain.dto.CartDTO;
import com.abc.business.domain.vo.CartVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    List<Cart> selectCartList(CartDTO cartDTO);

    List<CartVO> selectAllCartList();

    Long selectExistCart(@Param("userId") Long userId, @Param("productId") Long productId);

    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}

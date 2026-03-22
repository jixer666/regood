package com.abc.business.service;

import com.abc.business.domain.dto.CartDTO;
import com.abc.business.domain.entity.Cart;
import com.abc.business.domain.vo.CartVO;
import com.abc.common.domain.vo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CartService extends IService<Cart> {

    PageResult getCartPage(Long userId);

    void addToCart(CartDTO cartDTO, Long userId);

    void removeFromCart(Long cartId, Long userId);

    List<CartVO> getCartList(Long userId);
}

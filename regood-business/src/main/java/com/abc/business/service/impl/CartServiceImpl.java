package com.abc.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.abc.business.domain.dto.CartDTO;
import com.abc.business.domain.entity.Cart;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.CartVO;
import com.abc.business.mapper.CartMapper;
import com.abc.business.mapper.ProductMapper;
import com.abc.business.service.CartService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl extends BaseServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult getCartPage(Long userId) {
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        List<Cart> carts = baseMapper.selectCartList(buildDTO(userId));
        List<CartVO> voList = convertToCartVOList(carts);

        return PageResult.builder().list(voList).total((long) voList.size()).build();
    }

    @Override
    public PageResult getAllCartPage() {
        List<CartVO> voList = baseMapper.selectAllCartList();
        return PageResult.builder().list(voList).total((long) voList.size()).build();
    }

    @Override
    @Transactional
    public void addToCart(CartDTO cartDTO, Long userId) {
        AssertUtils.isNotEmpty(cartDTO.getProductId(), "商品ID不能为空");
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        Product product = productMapper.selectById(cartDTO.getProductId());
        AssertUtils.isNotEmpty(product, "商品不存在");
        AssertUtils.isFalse(product.getStatus() == 2, "商品已卖出");
        AssertUtils.isFalse(product.getStatus() == 3, "商品已下架");
        if (product.getAuditStatus() != null && product.getAuditStatus() != 1) {
            AssertUtils.isTrue(false, "商品未通过审核");
        }

        AssertUtils.isFalse(product.getSellerId().equals(userId), "不能添加自己发布的商品");

        Long existCartId = baseMapper.selectExistCart(userId, cartDTO.getProductId());
        AssertUtils.isEmpty(existCartId, "该商品已在购物车中");

        Cart cart = new Cart();
        cart.setCartId(IdUtils.getId());
        cart.setUserId(userId);
        cart.setProductId(cartDTO.getProductId());
        cart.setCreateTime(new java.util.Date());

        baseMapper.insert(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartId, Long userId) {
        AssertUtils.isNotEmpty(cartId, "购物车ID不能为空");

        baseMapper.deleteById(cartId);
    }

    @Override
    @Transactional
    public void batchRemoveFromCart(List<Long> cartIds) {
        if (cartIds != null && !cartIds.isEmpty()) {
            for (Long cartId : cartIds) {
                baseMapper.deleteById(cartId);
            }
        }
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<Cart> carts = baseMapper.selectCartList(buildDTO(userId));
        return convertToCartVOList(carts);
    }

    private CartDTO buildDTO(Long userId) {
        CartDTO dto = new CartDTO();
        dto.setUserId(userId);
        return dto;
    }

    private List<CartVO> convertToCartVOList(List<Cart> carts) {
        if (CollUtil.isEmpty(carts)) {
            return new ArrayList<>();
        }

        List<CartVO> voList = new ArrayList<>();
        for (Cart cart : carts) {
            CartVO vo = CartVO.builder()
                    .cartId(cart.getCartId())
                    .productId(cart.getProductId())
                    .createTime(cart.getCreateTime())
                    .title(cart.getTitle())
                    .price(cart.getPrice())
                    .condition(cart.getProductCondition())
                    .freeShipping(cart.getFreeShipping())
                    .sellerId(cart.getSellerId())
                    .sellerName(cart.getSellerName())
                    .sellerAvatar(cart.getSellerAvatar())
                    .build();

            if (cart.getImages() != null && !cart.getImages().isEmpty()) {
                try {
                    vo.setImages(JSONUtil.toList(cart.getImages(), String.class));
                } catch (Exception e) {
                    vo.setImages(java.util.Collections.singletonList(cart.getImages()));
                }
            }

            voList.add(vo);
        }
        return voList;
    }
}

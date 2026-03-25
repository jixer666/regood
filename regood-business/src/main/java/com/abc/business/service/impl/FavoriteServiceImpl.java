package com.abc.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.abc.business.domain.entity.Favorite;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.ProductVO;
import com.abc.business.mapper.FavoriteMapper;
import com.abc.business.mapper.ProductMapper;
import com.abc.business.service.FavoriteService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FavoriteServiceImpl extends BaseServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public void addFavorite(Long productId, Long userId) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        AssertUtils.isTrue(!isFavorite(productId, userId), "已收藏");

        Favorite favorite = new Favorite();
        favorite.setFavoriteId(IdUtils.getId());
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(new java.util.Date());

        favoriteMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId, Long userId) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }

    @Override
    public boolean isFavorite(Long productId, Long userId) {
        if (Objects.isNull(productId) || Objects.isNull(userId)) {
            return false;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<ProductVO> getFavoriteList(Long userId) {
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);

        List<ProductVO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(favorites)) {
            for (Favorite fav : favorites) {
                Product product = productMapper.selectById(fav.getProductId());
                if (product != null) {
                    ProductVO vo = convertToProductVO(product);
                    vo.setIsFavorite(true);
                    result.add(vo);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProductVO> getAllFavoriteList() {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Favorite::getCreateTime);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);

        List<ProductVO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(favorites)) {
            for (Favorite fav : favorites) {
                Product product = productMapper.selectById(fav.getProductId());
                if (product != null) {
                    ProductVO vo = convertToProductVO(product);
                    vo.setIsFavorite(true);
                    result.add(vo);
                }
            }
        }
        return result;
    }

    private ProductVO convertToProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setProductId(product.getProductId());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setCondition(product.getProductCondition());
        vo.setCategoryId(product.getCategoryId());
        vo.setSellerId(product.getSellerId());
        vo.setStatus(product.getStatus());
        vo.setFreeShipping(product.getFreeShipping());
        vo.setShippingFee(product.getShippingFee());
        vo.setCreateTime(product.getCreateTime());

        if (product.getImages() != null) {
            vo.setImages(JSONUtil.toList(product.getImages(), String.class));
        }

        return vo;
    }
}

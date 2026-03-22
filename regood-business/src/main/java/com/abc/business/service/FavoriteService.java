package com.abc.business.service;

import com.abc.business.domain.entity.Favorite;
import com.abc.business.domain.vo.ProductVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {

    void addFavorite(Long productId, Long userId);

    void removeFavorite(Long productId, Long userId);

    boolean isFavorite(Long productId, Long userId);

    List<ProductVO> getFavoriteList(Long userId);
}

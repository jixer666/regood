package com.abc.business.controller;

import com.abc.business.domain.vo.ProductVO;
import com.abc.business.service.FavoriteService;
import com.abc.common.domain.vo.ApiResult;
import com.abc.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "收藏接口")
@RestController
@RequestMapping("/business/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @ApiOperation("获取收藏列表")
    @GetMapping("/list")
    public ApiResult<List<ProductVO>> getFavoriteList() {
        Long userId = SecurityUtils.getUserId();
        List<ProductVO> list = favoriteService.getFavoriteList(userId);
        return ApiResult.success(list);
    }

    @ApiOperation("获取所有收藏列表(管理员)")
    @GetMapping("/all")
    public ApiResult<List<ProductVO>> getAllFavoriteList() {
        List<ProductVO> list = favoriteService.getAllFavoriteList();
        return ApiResult.success(list);
    }

    @ApiOperation("添加收藏")
    @PostMapping("/{productId}")
    public ApiResult<String> addFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getUserId();
        favoriteService.addFavorite(productId, userId);
        return ApiResult.success("收藏成功");
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/{productId}")
    public ApiResult<String> removeFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getUserId();
        favoriteService.removeFavorite(productId, userId);
        return ApiResult.success("取消收藏成功");
    }
}

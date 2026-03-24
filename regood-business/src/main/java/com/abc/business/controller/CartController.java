package com.abc.business.controller;

import com.abc.business.domain.dto.CartDTO;
import com.abc.business.domain.vo.CartVO;
import com.abc.business.service.CartService;
import com.abc.common.domain.vo.ApiResult;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "购物车接口")
@RestController
@RequestMapping("/business/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("获取购物车列表（当前用户）")
    @GetMapping("/list")
    public ApiResult<PageResult> getCartList() {
        Long userId = SecurityUtils.getUserId();
        PageResult pageResult = cartService.getCartPage(userId);
        return ApiResult.success(pageResult);
    }

    @ApiOperation("获取所有购物车列表（管理员）")
    @GetMapping("/all")
    public ApiResult<PageResult> getAllCartList() {
        PageResult pageResult = cartService.getAllCartPage();
        return ApiResult.success(pageResult);
    }

    @ApiOperation("添加购物车")
    @PostMapping
    public ApiResult<String> addToCart(@RequestBody CartDTO cartDTO) {
        Long userId = SecurityUtils.getUserId();
        cartService.addToCart(cartDTO, userId);
        return ApiResult.success("添加成功");
    }

    @ApiOperation("移除购物车")
    @DeleteMapping("/{cartId}")
    public ApiResult<String> removeFromCart(@PathVariable Long cartId) {
        Long userId = SecurityUtils.getUserId();
        cartService.removeFromCart(cartId, userId);
        return ApiResult.success("移除成功");
    }

    @ApiOperation("批量移除购物车")
    @DeleteMapping("/batch")
    public ApiResult<String> batchRemoveFromCart(@RequestBody List<Long> cartIds) {
        cartService.batchRemoveFromCart(cartIds);
        return ApiResult.success("批量移除成功");
    }
}

package com.abc.business.controller;

import com.abc.business.domain.dto.ProductDTO;
import com.abc.business.domain.vo.ProductVO;
import com.abc.business.service.ProductService;
import com.abc.common.domain.vo.ApiResult;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
@RestController
@RequestMapping("/business/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("查询商品分页")
    @GetMapping("/page")
    public ApiResult<PageResult> getProductPage(ProductDTO productDTO) {
        PageResult pageResult = productService.getProductPage(productDTO);
        return ApiResult.success(pageResult);
    }

    @ApiOperation("查询我的发布")
    @GetMapping("/my")
    public ApiResult<PageResult> getMyProducts(ProductDTO productDTO) {
        Long userId = SecurityUtils.getUserId();
        productDTO.setSellerId(userId);
        PageResult pageResult = productService.getProductPage(productDTO);
        return ApiResult.success(pageResult);
    }

    @ApiOperation("查询商品详情")
    @GetMapping("/detail")
    public ApiResult<ProductVO> getProductDetail(@RequestParam Long productId) {
        Long userId = SecurityUtils.getUserId();
        ProductVO productVO = productService.getProductDetail(productId, userId);
        return ApiResult.success(productVO);
    }

    @ApiOperation("发布商品")
    @PostMapping
    public ApiResult<String> saveProduct(@RequestBody ProductDTO productDTO) {
        Long userId = SecurityUtils.getUserId();
        productService.saveProduct(productDTO, userId);
        return ApiResult.success("发布成功");
    }

    @ApiOperation("修改商品")
    @PutMapping
    public ApiResult<String> updateProduct(@RequestBody ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return ApiResult.success("修改成功");
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/{productId}")
    public ApiResult<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ApiResult.success("删除成功");
    }

    @ApiOperation("下架商品")
    @PutMapping("/offline/{productId}")
    public ApiResult<String> offlineProduct(@PathVariable Long productId) {
        productService.updateProductStatus(productId, 3);
        return ApiResult.success("下架成功");
    }

    @ApiOperation("获取推荐商品")
    @GetMapping("/recommend")
    public ApiResult<List<ProductVO>> getRecommendProducts() {
        Long userId = SecurityUtils.getUserId();
        List<ProductVO> list = productService.getRecommendProducts(userId);
        return ApiResult.success(list);
    }

    @ApiOperation("添加收藏")
    @PostMapping("/favorite/{productId}")
    public ApiResult<String> addFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getUserId();
        productService.addFavorite(productId, userId);
        return ApiResult.success("收藏成功");
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/favorite/{productId}")
    public ApiResult<String> removeFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getUserId();
        productService.removeFavorite(productId, userId);
        return ApiResult.success("取消收藏成功");
    }
}

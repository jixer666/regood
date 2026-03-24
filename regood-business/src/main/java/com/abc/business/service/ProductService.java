package com.abc.business.service;

import com.abc.business.domain.dto.ProductDTO;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.ProductVO;
import com.abc.common.domain.vo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductService extends IService<Product> {

    PageResult getProductPage(ProductDTO productDTO);

    ProductVO getProductDetail(Long productId, Long userId);

    void saveProduct(ProductDTO productDTO, Long userId);

    void updateProduct(ProductDTO productDTO);

    void deleteProduct(Long productId);

    void updateProductStatus(Long productId, Integer status);

    PageResult getPendingAuditList(ProductDTO productDTO);

    void approveProduct(Long productId, String rejectReason);

    void rejectProduct(Long productId, String rejectReason);

    void addToCart(Long productId, Long userId);

    void removeFromCart(Long cartId);

    void addFavorite(Long productId, Long userId);

    void removeFavorite(Long productId, Long userId);

    List<ProductVO> getRecommendProducts(Long userId);
}

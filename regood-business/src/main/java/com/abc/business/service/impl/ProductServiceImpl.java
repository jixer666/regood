package com.abc.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.abc.business.convert.ProductConvert;
import com.abc.business.domain.dto.ProductDTO;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.ProductVO;
import com.abc.business.mapper.CartMapper;
import com.abc.business.mapper.ProductMapper;
import com.abc.business.service.FavoriteService;
import com.abc.business.service.ProductService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import com.abc.common.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Override
    public PageResult getProductPage(ProductDTO productDTO) {
        startPage();
        List<Product> products = productMapper.selectProductPage(productDTO);
        List<ProductVO> voList = convertToProductVOList(products);
        return buildPageResult(voList);
    }

    @Override
    public ProductVO getProductDetail(Long productId, Long userId) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");

        Product product = productMapper.selectByProduceId(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");

        ProductVO vo = ProductConvert.convertToProductVO(product);

        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(productId, userId));
        }

        return vo;
    }

    @Override
    @Transactional
    public void saveProduct(ProductDTO productDTO, Long userId) {
        AssertUtils.isNotEmpty(productDTO, "商品信息不能为空");
        AssertUtils.isNotEmpty(productDTO.getTitle(), "商品标题不能为空");
        AssertUtils.isNotEmpty(productDTO.getPrice(), "商品价格不能为空");

        Product product = ProductConvert.convertToProduct(productDTO);
        product.setProductId(IdUtils.getId());
        product.setSellerId(userId);
        product.setStatus(0);
        product.setAuditStatus(0);
        product.setViewCount(0);
        product.setWantCount(0);
        product.setImages(JSONUtil.toJsonStr(productDTO.getImages()));
        product.setTradeMethods(JSONUtil.toJsonStr(productDTO.getTradeMethods()));
        product.setCommonParams();

        productMapper.insert(product);
    }

    @Override
    @Transactional
    public void updateProduct(ProductDTO productDTO) {
        AssertUtils.isNotEmpty(productDTO.getProductId(), "商品ID不能为空");

        Product product = productMapper.selectById(productDTO.getProductId());
        AssertUtils.isNotEmpty(product, "商品不存在");

        AssertUtils.isTrue(product.getSellerId().equals(SecurityUtils.getUserId()), "无权限修改");

        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getOriginalPrice());
        product.setProductCondition(productDTO.getCondition());
        product.setCategoryId(productDTO.getCategoryId());
        product.setFreeShipping(productDTO.getFreeShipping());
        product.setShippingFee(productDTO.getShippingFee());
        product.setMeetingPlace(productDTO.getMeetingPlace());

        if (productDTO.getImages() != null) {
            product.setImages(JSONUtil.toJsonStr(productDTO.getImages()));
        }
        if (productDTO.getTradeMethods() != null) {
            product.setTradeMethods(JSONUtil.toJsonStr(productDTO.getTradeMethods()));
        }

        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");

        Product product = productMapper.selectById(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");

        AssertUtils.isTrue(product.getSellerId().equals(SecurityUtils.getUserId()), "无权限删除");

        productMapper.deleteById(productId);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long productId, Integer status) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");

        Product product = productMapper.selectById(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");

        AssertUtils.isTrue(product.getSellerId().equals(SecurityUtils.getUserId()) || SecurityUtils.getLoginUser().getUsername().equals("admin"), "无权限修改状态");

        product.setStatus(status);
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void addToCart(Long productId, Long userId) {
        AssertUtils.isNotEmpty(productId, "商品ID不能为空");
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        Product product = productMapper.selectById(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");
        AssertUtils.isTrue(product.getStatus() == 1, "商品已下架或已售出");

        LambdaQueryWrapper<com.abc.business.domain.entity.Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.abc.business.domain.entity.Cart::getUserId, userId)
               .eq(com.abc.business.domain.entity.Cart::getProductId, productId);
        long count = cartMapper.selectCount(wrapper);
        AssertUtils.isTrue(count == 0, "商品已在购物车中");

        com.abc.business.domain.entity.Cart cart = new com.abc.business.domain.entity.Cart();
        cart.setCartId(IdUtils.getId());
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setCreateTime(new java.util.Date());

        cartMapper.insert(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartId) {
        AssertUtils.isNotEmpty(cartId, "购物车ID不能为空");
        baseMapper.deleteById(cartId);
    }

    @Override
    @Transactional
    public void addFavorite(Long productId, Long userId) {
        favoriteService.addFavorite(productId, userId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId, Long userId) {
        favoriteService.removeFavorite(productId, userId);
    }

    @Override
    public List<ProductVO> getRecommendProducts(Long userId) {
        ProductDTO dto = new ProductDTO();
        dto.setStatus(1);
        startPage();
        List<Product> products = productMapper.selectProductPage(dto);
        return convertToProductVOList(products);
    }

    private List<ProductVO> convertToProductVOList(List<Product> products) {
        if (CollUtil.isEmpty(products)) {
            return new ArrayList<>();
        }
        List<ProductVO> voList = new ArrayList<>();
        for (Product product : products) {
            voList.add(ProductConvert.convertToProductVO(product));
        }
        return voList;
    }

    @Override
    public PageResult getPendingAuditList(ProductDTO productDTO) {
        if (productDTO == null) {
            productDTO = new ProductDTO();
        }
        productDTO.setAuditStatus(0);
        startPage();
        List<Product> products = productMapper.selectProductPage(productDTO);
        List<ProductVO> voList = convertToProductVOList(products);
        return buildPageResult(voList);
    }

    @Override
    public void approveProduct(Long productId, String rejectReason) {
        Product product = productMapper.selectById(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");
        AssertUtils.isTrue(product.getAuditStatus() == 0, "商品不是待审核状态");
        
        product.setAuditStatus(1);
        product.setStatus(1);
        product.setRejectReason(null);
        productMapper.updateById(product);
    }

    @Override
    public void rejectProduct(Long productId, String rejectReason) {
        Product product = productMapper.selectById(productId);
        AssertUtils.isNotEmpty(product, "商品不存在");
        AssertUtils.isTrue(product.getAuditStatus() == 0, "商品不是待审核状态");
        
        product.setAuditStatus(2);
        product.setRejectReason(rejectReason);
        productMapper.updateById(product);
    }
}

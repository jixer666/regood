package com.abc.business.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.abc.business.domain.dto.ProductDTO;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.ProductVO;

import java.util.ArrayList;
import java.util.List;

public class ProductConvert {

    public static Product convertToProduct(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = BeanUtil.copyProperties(dto, Product.class);
        return product;
    }

    public static ProductVO convertToProductVO(Product product) {
        if (product == null) {
            return null;
        }

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
        vo.setViewCount(product.getViewCount());
        vo.setWantCount(product.getWantCount());
        vo.setMeetingPlace(product.getMeetingPlace());
        vo.setFreeShipping(product.getFreeShipping());
        vo.setShippingFee(product.getShippingFee());
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        vo.setTradeMethods(product.getTradeMethods());
        vo.setSellerName(product.getSellerName());
        vo.setSellerId(product.getSellerId());
        vo.setSellerAvatar(product.getSellerAvatar());

        if (product.getImages() != null) {
            vo.setImages(JSONUtil.toList(product.getImages(), String.class));
        }

        return vo;
    }

    public static List<ProductVO> convertToProductVOList(List<Product> products) {
        if (products == null) {
            return null;
        }
        List<ProductVO> voList = new ArrayList<>();
        for (Product product : products) {
            voList.add(convertToProductVO(product));
        }
        return voList;
    }
}

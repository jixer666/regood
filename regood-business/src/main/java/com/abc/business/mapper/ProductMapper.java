package com.abc.business.mapper;

import com.abc.business.domain.entity.Product;
import com.abc.business.domain.dto.ProductDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> selectProductPage(ProductDTO productDTO);

    List<Product> selectProductList(ProductDTO productDTO);

    Product selectByProduceId(Long productId);
}

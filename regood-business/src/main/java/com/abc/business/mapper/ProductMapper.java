package com.abc.business.mapper;

import com.abc.business.domain.entity.Product;
import com.abc.business.domain.dto.ProductDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    List<Product> selectProductPage(ProductDTO productDTO);

    List<Product> selectProductList(ProductDTO productDTO);

    Product selectByProduceId(Long productId);

    List<Long> selectCategoryIdsByParentId(@Param("parentId") Long parentId);

    Long selectTotalCount();

    Long selectPendingAuditCount();

    Long selectTodayCount();

    List<Map<String, Object>> selectProductTrend();

    List<Map<String, Object>> selectCategoryDistribution();
}

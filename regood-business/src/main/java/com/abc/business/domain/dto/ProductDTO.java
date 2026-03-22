package com.abc.business.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {

    private Long productId;

    private String title;

    private String description;

    private List<String> images;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String condition;

    private Long categoryId;

    private Long sellerId;

    private Integer status;

    private String tradeMethods;

    private String meetingPlace;

    private Boolean freeShipping;

    private BigDecimal shippingFee;

    private Integer pageNum;

    private Integer pageSize;

    private String sortType;

    private Long categoryIdForQuery;
}

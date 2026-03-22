package com.abc.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVO {

    private Long productId;

    private String title;

    private String description;

    private List<String> images;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String condition;

    private Long categoryId;

    private String categoryName;

    private Long sellerId;

    private String sellerName;

    private String sellerAvatar;

    private Integer sellerPublishCount;

    private Integer sellerSoldCount;

    private Integer status;

    private Integer viewCount;

    private Integer wantCount;

    private String tradeMethods;

    private String meetingPlace;

    private Boolean freeShipping;

    private BigDecimal shippingFee;

    private Date createTime;

    private Date updateTime;

    private Boolean isFavorite;
}

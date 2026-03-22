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
public class CartVO {

    private Long cartId;

    private Long productId;

    private String title;

    private List<String> images;

    private BigDecimal price;

    private String condition;

    private String sellerName;

    private String sellerAvatar;

    private Long sellerId;

    private Boolean freeShipping;

    private Date createTime;
}

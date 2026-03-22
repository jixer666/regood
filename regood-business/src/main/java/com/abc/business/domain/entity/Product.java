package com.abc.business.domain.entity;

import com.abc.common.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("tb_product")
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

    @TableId
    private Long productId;

    private String title;

    private String description;

    private String images;

    private java.math.BigDecimal price;

    private java.math.BigDecimal originalPrice;

    private String productCondition;

    private Long categoryId;

    private Long sellerId;

    private Integer status;

    private Integer viewCount;

    private Integer wantCount;

    private String tradeMethods;

    private String meetingPlace;

    private Boolean freeShipping;

    private java.math.BigDecimal shippingFee;

    @TableField(exist = false)
    private String sellerName;

    @TableField(exist = false)
    private String sellerAvatar;

    @TableField(exist = false)
    private String categoryName;
}

package com.abc.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@TableName("tb_cart")
@AllArgsConstructor
@NoArgsConstructor
public class Cart implements Serializable {

    @TableId
    private Long cartId;

    private Long userId;

    private Long productId;

    private Date createTime;

    @TableField(exist = false)
    private String title;

    @TableField(exist = false)
    private String images;

    @TableField(exist = false)
    private BigDecimal price;

    @TableField(exist = false)
    private String productCondition;

    @TableField(exist = false)
    private Boolean freeShipping;

    @TableField(exist = false)
    private Long sellerId;

    @TableField(exist = false)
    private String sellerName;

    @TableField(exist = false)
    private String sellerAvatar;
}

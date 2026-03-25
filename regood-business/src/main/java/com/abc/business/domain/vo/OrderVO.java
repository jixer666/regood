package com.abc.business.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
public class OrderVO {

    private Long orderId;

    private String orderNo;

    private Long productId;

    private String productTitle;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> productImages;

    private BigDecimal price;

    private String categoryName;

    private String productCondition;

    private String sellerPhone;

    private Integer status;

    private String statusText;

    private String tradeMethod;

    private String address;

    private String meetingPlace;

    private Date payTime;

    private Date createTime;

    private Date updateTime;

    private Long buyerId;

    private String buyerName;

    private Long sellerId;

    private String sellerName;
}

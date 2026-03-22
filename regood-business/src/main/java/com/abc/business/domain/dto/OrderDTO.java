package com.abc.business.domain.dto;

import lombok.Data;

@Data
public class OrderDTO {

    private Long orderId;

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private Long productId;

    private java.math.BigDecimal price;

    private Integer status;

    private String tradeMethod;

    private String address;

    private String meetingPlace;

    public void checkSaveParams() {
        com.abc.common.util.AssertUtils.isNotEmpty(this, "订单参数不能为空");
        com.abc.common.util.AssertUtils.isNotEmpty(productId, "商品ID不能为空");
        com.abc.common.util.AssertUtils.isNotEmpty(tradeMethod, "交易方式不能为空");
    }
}

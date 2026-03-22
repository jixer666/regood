package com.abc.business.domain.entity;

import com.abc.common.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("tb_order")
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @TableId
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

    private Date payTime;
}

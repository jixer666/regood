package com.abc.business.domain.dto;

import lombok.Data;

@Data
public class CartDTO {

    private Long cartId;

    private Long userId;

    private Long productId;
}

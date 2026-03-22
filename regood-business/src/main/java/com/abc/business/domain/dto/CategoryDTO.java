package com.abc.business.domain.dto;

import lombok.Data;

@Data
public class CategoryDTO {

    private Long categoryId;

    private String name;

    private String icon;

    private Long parentId;

    private Integer sort;

    private Integer status;
}

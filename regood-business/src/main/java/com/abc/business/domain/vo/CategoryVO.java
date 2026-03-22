package com.abc.business.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {

    private Long categoryId;

    private String name;

    private String icon;

    private Long parentId;

    private List<CategoryVO> children;
}

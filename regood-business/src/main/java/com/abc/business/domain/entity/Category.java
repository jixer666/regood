package com.abc.business.domain.entity;

import com.abc.common.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("tb_category")
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @TableId
    private Long categoryId;

    private String name;

    private String icon;

    private Long parentId;

    private Integer sort;
}

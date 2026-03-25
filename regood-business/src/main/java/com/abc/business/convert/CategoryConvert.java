package com.abc.business.convert;

import com.abc.business.domain.dto.CategoryDTO;
import com.abc.business.domain.entity.Category;
import com.abc.business.domain.vo.CategoryVO;

public class CategoryConvert {

    public static Category convertToCategory(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setCategoryId(dto.getCategoryId());
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setParentId(dto.getParentId());
        category.setSort(dto.getSort());
        return category;
    }

    public static CategoryVO convertToCategoryVO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryVO vo = new CategoryVO();
        vo.setCategoryId(category.getCategoryId());
        vo.setName(category.getName());
        vo.setIcon(category.getIcon());
        vo.setParentId(category.getParentId());
        vo.setSort(category.getSort());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }
}

package com.abc.business.service;

import com.abc.business.domain.dto.CategoryDTO;
import com.abc.business.domain.entity.Category;
import com.abc.business.domain.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryVO> getCategoryTree();

    List<Category> getCategoryList(CategoryDTO categoryDTO);

    List<CategoryVO> getCategoryVOList(CategoryDTO categoryDTO);

    void saveCategory(CategoryDTO categoryDTO);

    void updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long categoryId);
}

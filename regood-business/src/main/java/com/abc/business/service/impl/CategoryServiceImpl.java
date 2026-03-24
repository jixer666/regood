package com.abc.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.abc.business.convert.CategoryConvert;
import com.abc.business.domain.dto.CategoryDTO;
import com.abc.business.domain.entity.Category;
import com.abc.business.domain.vo.CategoryVO;
import com.abc.business.mapper.CategoryMapper;
import com.abc.business.service.CategoryService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> allCategories = baseMapper.selectCategoryList(new CategoryDTO());

        List<Category> parentCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());

        List<CategoryVO> result = new ArrayList<>();
        for (Category parent : parentCategories) {
            CategoryVO parentVO = CategoryConvert.convertToCategoryVO(parent);

            List<Category> children = allCategories.stream()
                    .filter(c -> parent.getCategoryId().equals(c.getParentId()))
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(children)) {
                List<CategoryVO> childVOs = new ArrayList<>();
                for (Category child : children) {
                    childVOs.add(CategoryConvert.convertToCategoryVO(child));
                }
                parentVO.setChildren(childVOs);
            }

            result.add(parentVO);
        }

        return result;
    }

    @Override
    public List<Category> getCategoryList(CategoryDTO categoryDTO) {
        return baseMapper.selectCategoryList(categoryDTO);
    }

    @Override
    public List<CategoryVO> getCategoryVOList(CategoryDTO categoryDTO) {
        List<Category> list = baseMapper.selectCategoryList(categoryDTO);
        List<CategoryVO> voList = new ArrayList<>();
        for (Category category : list) {
            voList.add(CategoryConvert.convertToCategoryVO(category));
        }
        return voList;
    }

    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        AssertUtils.isNotEmpty(categoryDTO.getName(), "分类名称不能为空");
        
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        if (category.getCategoryId() == null) {
            category.setCategoryId(IdUtils.getId());
            category.setCommonParams();
            baseMapper.insert(category);
        } else {
            baseMapper.updateById(category);
        }
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        AssertUtils.isNotEmpty(categoryDTO.getCategoryId(), "分类ID不能为空");
        AssertUtils.isNotEmpty(categoryDTO.getName(), "分类名称不能为空");
        
        Category category = baseMapper.selectById(categoryDTO.getCategoryId());
        AssertUtils.isNotEmpty(category, "分类不存在");
        
        BeanUtil.copyProperties(categoryDTO, category);
        baseMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        AssertUtils.isNotEmpty(categoryId, "分类ID不能为空");
        
        Category category = baseMapper.selectById(categoryId);
        AssertUtils.isNotEmpty(category, "分类不存在");
        
        baseMapper.deleteById(categoryId);
    }
}

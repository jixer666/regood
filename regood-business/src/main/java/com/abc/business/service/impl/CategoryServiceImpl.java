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
}

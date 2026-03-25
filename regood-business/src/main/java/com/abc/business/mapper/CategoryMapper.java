package com.abc.business.mapper;

import com.abc.business.domain.entity.Category;
import com.abc.business.domain.dto.CategoryDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<Category> selectCategoryList(CategoryDTO categoryDTO);

    List<Long> selectCategoryIdsByParentId(@Param("parentId") Long parentId);
}

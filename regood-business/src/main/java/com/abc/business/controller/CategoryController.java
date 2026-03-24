package com.abc.business.controller;

import com.abc.business.domain.dto.CategoryDTO;
import com.abc.business.domain.vo.CategoryVO;
import com.abc.business.service.CategoryService;
import com.abc.common.domain.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "分类接口")
@RestController
@RequestMapping("/business/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public ApiResult<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> tree = categoryService.getCategoryTree();
        return ApiResult.success(tree);
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public ApiResult<List<CategoryVO>> getCategoryList(CategoryDTO categoryDTO) {
        List<CategoryVO> list = categoryService.getCategoryVOList(categoryDTO);
        return ApiResult.success(list);
    }

    @ApiOperation("新增分类")
    @PostMapping
    public ApiResult<String> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.saveCategory(categoryDTO);
        return ApiResult.success("保存成功");
    }

    @ApiOperation("修改分类")
    @PutMapping
    public ApiResult<String> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryDTO);
        return ApiResult.success("修改成功");
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{categoryId}")
    public ApiResult<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResult.success("删除成功");
    }
}

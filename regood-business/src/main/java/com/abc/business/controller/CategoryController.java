package com.abc.business.controller;

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
}

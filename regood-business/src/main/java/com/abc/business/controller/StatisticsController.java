package com.abc.business.controller;

import com.abc.business.domain.vo.DashboardStatisticsVO;
import com.abc.business.mapper.OrderMapper;
import com.abc.business.mapper.ProductMapper;
import com.abc.business.mapper.CategoryMapper;
import com.abc.common.domain.vo.ApiResult;
import com.abc.system.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "数据统计接口")
@RestController
@RequestMapping("/business/statistics")
public class StatisticsController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @ApiOperation("获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public ApiResult<DashboardStatisticsVO> getDashboardStatistics() {
        Long productCount = productMapper.selectTotalCount();
        Long pendingAuditCount = productMapper.selectPendingAuditCount();
        Long orderCount = orderMapper.selectTotalCount();
        Long userCount = Long.valueOf(userMapper.selectCount(new LambdaQueryWrapper<>()));
        Long todayProductCount = productMapper.selectTodayCount();
        Long todayOrderCount = orderMapper.selectTodayCount();

        List<Map<String, Object>> productTrend = productMapper.selectProductTrend();
        List<Map<String, Object>> orderTrend = orderMapper.selectOrderTrend();
        List<Map<String, Object>> categoryDistribution = productMapper.selectCategoryDistribution();

        DashboardStatisticsVO vo = DashboardStatisticsVO.builder()
                .productCount(productCount)
                .pendingAuditCount(pendingAuditCount)
                .orderCount(orderCount)
                .userCount(userCount)
                .todayProductCount(todayProductCount)
                .todayOrderCount(todayOrderCount)
                .productTrend(productTrend)
                .orderTrend(orderTrend)
                .categoryDistribution(categoryDistribution)
                .build();

        return ApiResult.success(vo);
    }
}

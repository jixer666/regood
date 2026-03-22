package com.abc.business.controller;

import com.abc.business.domain.dto.OrderDTO;
import com.abc.business.domain.vo.OrderVO;
import com.abc.business.service.OrderService;
import com.abc.common.domain.vo.ApiResult;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "订单接口")
@RestController
@RequestMapping("/business/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("查询订单分页")
    @GetMapping("/page")
    public ApiResult<PageResult> getOrderPage(OrderDTO orderDTO) {
        Long userId = SecurityUtils.getUserId();
        PageResult pageResult = orderService.getOrderPage(orderDTO, userId);
        return ApiResult.success(pageResult);
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/detail")
    public ApiResult<OrderVO> getOrderDetail(@RequestParam Long orderId) {
        Long userId = SecurityUtils.getUserId();
        OrderVO orderVO = orderService.getOrderDetail(orderId, userId);
        return ApiResult.success(orderVO);
    }

    @ApiOperation("创建订单")
    @PostMapping
    public ApiResult<String> createOrder(@RequestBody OrderDTO orderDTO) {
        Long userId = SecurityUtils.getUserId();
        orderService.createOrder(orderDTO, userId);
        return ApiResult.success("下单成功");
    }

    @ApiOperation("取消订单")
    @PutMapping("/cancel/{orderId}")
    public ApiResult<String> cancelOrder(@PathVariable Long orderId) {
        Long userId = SecurityUtils.getUserId();
        orderService.cancelOrder(orderId, userId);
        return ApiResult.success("取消成功");
    }

    @ApiOperation("支付订单")
    @PutMapping("/pay/{orderId}")
    public ApiResult<String> payOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, 2);
        return ApiResult.success("支付成功");
    }

    @ApiOperation("确认收货")
    @PutMapping("/confirm/{orderId}")
    public ApiResult<String> confirmOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, 3);
        return ApiResult.success("确认成功");
    }

    @ApiOperation("获取已卖出列表")
    @GetMapping("/sold")
    public ApiResult<List<OrderVO>> getMySoldList() {
        Long userId = SecurityUtils.getUserId();
        List<OrderVO> list = orderService.getMySoldList(userId);
        return ApiResult.success(list);
    }

    @ApiOperation("获取已买到列表")
    @GetMapping("/bought")
    public ApiResult<List<OrderVO>> getMyBoughtList() {
        Long userId = SecurityUtils.getUserId();
        List<OrderVO> list = orderService.getMyBoughtList(userId);
        return ApiResult.success(list);
    }
}

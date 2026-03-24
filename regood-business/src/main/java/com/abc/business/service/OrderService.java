package com.abc.business.service;

import com.abc.business.domain.dto.OrderDTO;
import com.abc.business.domain.entity.Order;
import com.abc.business.domain.vo.OrderVO;
import com.abc.common.domain.vo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderService extends IService<Order> {

    PageResult getOrderPage(OrderDTO orderDTO, Long userId);

    PageResult getAllOrderPage(OrderDTO orderDTO);

    OrderVO getOrderDetail(Long orderId, Long userId);

    void createOrder(OrderDTO orderDTO, Long userId);

    void updateOrderStatus(Long orderId, Integer status);

    void cancelOrder(Long orderId, Long userId);

    List<OrderVO> getMySoldList(Long userId);

    List<OrderVO> getMyBoughtList(Long userId);
}

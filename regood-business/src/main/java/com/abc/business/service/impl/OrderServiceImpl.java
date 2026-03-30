package com.abc.business.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.abc.business.domain.dto.OrderDTO;
import com.abc.business.domain.entity.Order;
import com.abc.business.domain.entity.Product;
import com.abc.business.domain.vo.OrderVO;
import com.abc.business.mapper.CartMapper;
import com.abc.business.mapper.OrderMapper;
import com.abc.business.mapper.ProductMapper;
import com.abc.business.service.OrderService;
import com.abc.common.core.service.BaseServiceImpl;
import com.abc.common.domain.vo.PageResult;
import com.abc.common.util.AssertUtils;
import com.abc.common.util.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Override
    public PageResult getOrderPage(OrderDTO orderDTO, Long userId) {
        AssertUtils.isNotEmpty(userId, "用户ID不能为空");

        orderDTO.setBuyerId(userId);
        List<Order> orders = orderMapper.selectOrderList(orderDTO);
        List<OrderVO> voList = convertToOrderVOList(orders);

        return PageResult.builder().list(voList).total((long) voList.size()).build();
    }

    @Override
    public PageResult getAllOrderPage(OrderDTO orderDTO) {
        List<OrderVO> voList = orderMapper.selectAllOrderList(orderDTO);
        return PageResult.builder().list(voList).total((long) voList.size()).build();
    }

    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        AssertUtils.isNotEmpty(orderId, "订单ID不能为空");

        Order order = orderMapper.selectById(orderId);
        AssertUtils.isNotEmpty(order, "订单不存在");

        return convertToOrderVO(order);
    }

    @Override
    @Transactional
    public void createOrder(OrderDTO orderDTO, Long userId) {
        orderDTO.checkSaveParams();

        Product product = productMapper.selectById(orderDTO.getProductId());
        AssertUtils.isNotEmpty(product, "商品不存在");
        AssertUtils.isTrue(product.getStatus() == 1, "商品已下架或已售出");
        AssertUtils.isTrue(!product.getSellerId().equals(userId), "不能购买自己的商品");

        Order order = new Order();
        order.setOrderId(IdUtils.getId());
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(userId);
        order.setSellerId(product.getSellerId());
        order.setProductId(product.getProductId());
        order.setPrice(product.getPrice());
        order.setStatus(1);
        order.setTradeMethod(orderDTO.getTradeMethod());
        order.setMeetingPlace(orderDTO.getMeetingPlace());
        order.setAddress(orderDTO.getAddress());
        order.setCommonParams();

        orderMapper.insert(order);

        product.setStatus(2);
        productMapper.updateById(product);

        cartMapper.deleteByUserIdAndProductId(userId, orderDTO.getProductId());
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, Integer status) {
        AssertUtils.isNotEmpty(orderId, "订单ID不能为空");

        Order order = orderMapper.selectById(orderId);
        AssertUtils.isNotEmpty(order, "订单不存在");

        order.setStatus(status);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        AssertUtils.isNotEmpty(orderId, "订单ID不能为空");

        Order order = orderMapper.selectById(orderId);
        AssertUtils.isNotEmpty(order, "订单不存在");
        AssertUtils.isTrue(order.getBuyerId().equals(userId), "无权限取消");
        AssertUtils.isTrue(order.getStatus() == 1, "订单已支付或已完成，无法取消");

        order.setStatus(4);
        orderMapper.updateById(order);

        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStatus(1);
            productMapper.updateById(product);
        }
    }

    @Override
    public List<OrderVO> getMySoldList(Long userId) {
        OrderDTO dto = new OrderDTO();
        dto.setSellerId(userId);
        List<OrderVO> list = orderMapper.selectAllOrderList(dto);
        return list;
    }

    @Override
    public List<OrderVO> getMyBoughtList(Long userId) {
        OrderDTO dto = new OrderDTO();
        dto.setBuyerId(userId);
        List<OrderVO> list = orderMapper.selectAllOrderList(dto);
        return list;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtils.getId() % 10000;
    }

    private List<OrderVO> convertToOrderVOList(List<Order> orders) {
        if (CollUtil.isEmpty(orders)) {
            return new ArrayList<>();
        }

        List<OrderVO> voList = new ArrayList<>();
        for (Order order : orders) {
            voList.add(convertToOrderVO(order));
        }
        return voList;
    }

    private OrderVO convertToOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setProductId(order.getProductId());
        vo.setPrice(order.getPrice());
        vo.setStatus(order.getStatus());
        vo.setTradeMethod(order.getTradeMethod());
        vo.setAddress(order.getAddress());
        vo.setMeetingPlace(order.getMeetingPlace());
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        if (order.getStatus() != null) {
            Map<Integer, String> statusMap = new HashMap<>();
            statusMap.put(1, "待支付");
            statusMap.put(2, "已支付");
            statusMap.put(3, "已完成");
            statusMap.put(4, "已取消");
            vo.setStatusText(statusMap.get(order.getStatus()));
        }

        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            if (product.getImages() != null) {
                vo.setProductImages(JSONUtil.toList(product.getImages(), String.class));
            }
        }

        return vo;
    }
}

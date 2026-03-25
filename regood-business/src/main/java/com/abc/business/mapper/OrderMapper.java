package com.abc.business.mapper;

import com.abc.business.domain.entity.Order;
import com.abc.business.domain.dto.OrderDTO;
import com.abc.business.domain.vo.OrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> selectOrderList(OrderDTO orderDTO);

    List<OrderVO> selectAllOrderList(OrderDTO orderDTO);

    Long selectTotalCount();

    Long selectTodayCount();

    List<Map<String, Object>> selectOrderTrend();
}

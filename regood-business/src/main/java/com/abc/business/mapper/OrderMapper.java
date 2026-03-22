package com.abc.business.mapper;

import com.abc.business.domain.entity.Order;
import com.abc.business.domain.dto.OrderDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> selectOrderList(OrderDTO orderDTO);
}

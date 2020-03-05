package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.model.dto.OrderDto;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.vo.OrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
    List<OrderVo> orders(Page page, @Param("qo") OrderDto qo);
    OrderVo order( @Param("orderId")Integer orderId);
    OrderVo getOrderByOrderNo( @Param("orderNo")String orderNo);
}

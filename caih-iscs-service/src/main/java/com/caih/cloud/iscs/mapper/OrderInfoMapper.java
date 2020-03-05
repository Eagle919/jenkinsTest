package com.caih.cloud.iscs.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}

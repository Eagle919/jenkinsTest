package com.caih.cloud.iscs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.OrderDto;
import com.caih.cloud.iscs.model.entity.Extract;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.vo.OrderVo;

public interface OrderService extends IService<OrderInfo> {
    Page<OrderVo> orderVos(OrderDto qo);
    Pair<Boolean,OrderVo> order(Integer id);
}

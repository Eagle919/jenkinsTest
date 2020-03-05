package com.caih.cloud.iscs.charge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.charge.model.dto.EOrderDto;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.vo.EOrderVo;
import com.caih.cloud.iscs.common.Pair;

public interface EOrderService extends IService<EOrder> {

    Page<EOrderVo> orders(EOrderDto dto);

}

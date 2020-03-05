package com.caih.cloud.iscs.charge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.charge.model.dto.EDeviceDto;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.vo.EDeviceVo;
import com.caih.cloud.iscs.common.Pair;

public interface EDeviceService extends IService<EDevice> {

    Page<EDeviceVo> devices(EDeviceDto dto);


}

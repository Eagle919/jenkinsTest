package com.caih.cloud.iscs.charge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.charge.model.dto.ECommunityDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.vo.ECommunityVo;
import com.caih.cloud.iscs.common.Pair;

public interface ECommunityService extends IService<ECommunity> {

    Page<ECommunityVo> communitys(ECommunityDto dto);

}

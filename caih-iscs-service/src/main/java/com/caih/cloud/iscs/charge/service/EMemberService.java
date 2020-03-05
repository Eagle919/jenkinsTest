package com.caih.cloud.iscs.charge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.charge.model.dto.EMemberDto;
import com.caih.cloud.iscs.charge.model.entity.EMember;
import com.caih.cloud.iscs.charge.model.vo.EMemberVo;
import com.caih.cloud.iscs.common.Pair;

public interface EMemberService extends IService<EMember> {

    Page<EMemberVo> members(EMemberDto dto);

}

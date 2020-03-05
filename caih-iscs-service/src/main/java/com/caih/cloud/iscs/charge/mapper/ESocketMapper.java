package com.caih.cloud.iscs.charge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ESocketMapper extends BaseMapper<ESocket> {
}

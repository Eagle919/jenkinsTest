package com.caih.cloud.iscs.charge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.charge.model.entity.EWxSuccessRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface EWxSuccessRecordMapper extends BaseMapper<EWxSuccessRecord> {
}

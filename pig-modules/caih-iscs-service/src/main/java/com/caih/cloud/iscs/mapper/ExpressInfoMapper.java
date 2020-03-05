package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.model.vo.ExpressInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ExpressInfoMapper extends BaseMapper<ExpressInfoVo> {
    String getExpressName(@Param("expressComNo") String expressComNo);
}

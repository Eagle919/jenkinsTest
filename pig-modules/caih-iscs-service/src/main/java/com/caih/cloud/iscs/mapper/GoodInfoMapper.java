package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.model.vo.GoodInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface GoodInfoMapper extends BaseMapper<GoodInfoVo> {
    List<GoodInfoVo> goodInfos(@Param("orderNo") String orderNo);
}

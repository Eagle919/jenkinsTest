package com.caih.cloud.iscs.charge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.ECommunityDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.vo.ECommunityVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ECommunityMapper extends BaseMapper<ECommunity> {
    List<ECommunityVo> communitys(Page<ECommunityVo> page, @Param("dto") ECommunityDto dto);
}

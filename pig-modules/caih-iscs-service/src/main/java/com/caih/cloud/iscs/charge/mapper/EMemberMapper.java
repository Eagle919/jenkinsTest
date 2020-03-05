package com.caih.cloud.iscs.charge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.EMemberDto;
import com.caih.cloud.iscs.charge.model.entity.EMember;
import com.caih.cloud.iscs.charge.model.vo.EMemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface EMemberMapper extends BaseMapper<EMember> {
    List<EMember> members(Page<EMemberVo> page, @Param("dto")EMemberDto dto);
}

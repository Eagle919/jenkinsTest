package com.caih.cloud.iscs.charge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.EDeviceDto;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.vo.EDeviceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface EDeviceMapper extends BaseMapper<EDevice> {
    Integer countDeviceNumber(@Param("communityId")Integer communityId);

    Integer countOnlineDeviceNumber(@Param("communityId")Integer communityId);

    List<EDeviceVo> devices(Page<EDeviceVo> page, @Param("dto")EDeviceDto dto);
}

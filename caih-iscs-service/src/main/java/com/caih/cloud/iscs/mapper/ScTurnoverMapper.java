package com.caih.cloud.iscs.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.model.entity.TurnoverInfo;
import com.caih.cloud.iscs.model.qo.ScTurnoverQo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Component
@Mapper
public interface ScTurnoverMapper extends BaseMapper<TurnoverInfo> {
    List<TurnoverInfo> list(Page page, @Param("qo") ScTurnoverQo qo);
}

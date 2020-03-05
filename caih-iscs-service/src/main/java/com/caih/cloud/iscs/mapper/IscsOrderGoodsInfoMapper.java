package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.model.entity.OrderGoodsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Component
@Mapper
public interface IscsOrderGoodsInfoMapper extends BaseMapper<OrderGoodsInfo> {

}

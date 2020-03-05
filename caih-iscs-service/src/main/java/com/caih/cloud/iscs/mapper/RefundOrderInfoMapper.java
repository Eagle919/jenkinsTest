package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.model.entity.RefundOrderInfo;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.qo.ScStoreQo;
import com.caih.cloud.iscs.model.qo.StoreQo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RefundOrderInfoMapper extends BaseMapper<RefundOrderInfo> {
}

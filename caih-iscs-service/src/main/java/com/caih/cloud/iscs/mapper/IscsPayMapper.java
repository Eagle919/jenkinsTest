package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caih.cloud.iscs.model.dto.TempOrderQrcodeDto;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Component
@Mapper
public interface IscsPayMapper extends BaseMapper<OrderInfo> {
    TempOrderQrcodeDto queryByValidKey(@Param("validKey") String validKey);
    TempOrderQrcodeDto queryByOrderNo(@Param("orderNo") String orderNo);
    void insertOrderQrcode(@Param("obj") TempOrderQrcode obj);
}

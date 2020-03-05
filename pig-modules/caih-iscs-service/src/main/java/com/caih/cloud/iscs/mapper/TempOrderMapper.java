package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import com.caih.cloud.iscs.model.qo.PaymentRecordQo;
import com.caih.cloud.iscs.model.vo.PaymentRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Component
@Mapper
public interface TempOrderMapper extends BaseMapper<TempOrderQrcode> {
    List<PaymentRecordVo> list(Page page, @Param("qo") PaymentRecordQo qo);
}

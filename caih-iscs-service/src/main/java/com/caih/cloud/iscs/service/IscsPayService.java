package com.caih.cloud.iscs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.model.dto.TempOrderQrcodeDto;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import com.caih.cloud.iscs.model.qo.PaymentRecordQo;
import com.caih.cloud.iscs.model.vo.PaymentRecordVo;

import java.util.Map;

/**
 * @author Kang
 * @date 2019/08/08
 */
public interface IscsPayService extends IService<OrderInfo> {
    TempOrderQrcodeDto queryByValidKey(String validKey);
    TempOrderQrcodeDto queryByOrderNo(String orderNo);
    void insertOrderQrcode(TempOrderQrcode obj);
    void orderResultNotify(Map<String, String> paramMap, String retUrl);
    Page<PaymentRecordVo> list(PaymentRecordQo qo);
}

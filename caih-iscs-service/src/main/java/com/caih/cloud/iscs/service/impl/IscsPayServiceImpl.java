package com.caih.cloud.iscs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.common.NotifyThread;
import com.caih.cloud.iscs.mapper.IscsPayMapper;
import com.caih.cloud.iscs.mapper.TempOrderMapper;
import com.caih.cloud.iscs.model.dto.TempOrderQrcodeDto;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.entity.TempOrderQrcode;
import com.caih.cloud.iscs.model.qo.PaymentRecordQo;
import com.caih.cloud.iscs.model.vo.PaymentRecordVo;
import com.caih.cloud.iscs.service.IscsPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Service
public class IscsPayServiceImpl extends ServiceImpl<IscsPayMapper, OrderInfo> implements IscsPayService {

    @Autowired
    private TempOrderMapper tempOrderMapper;

    @Override
    public TempOrderQrcodeDto queryByValidKey(String validKey){
        return baseMapper.queryByValidKey(validKey);
    }

    @Override
    public TempOrderQrcodeDto queryByOrderNo(String orderNo){
        return baseMapper.queryByOrderNo(orderNo);
    }

    @Override
    public void insertOrderQrcode(TempOrderQrcode obj){
        baseMapper.insertOrderQrcode(obj);
    }

    @Override
    public void orderResultNotify(Map<String, String> paramMap, String retUrl){
        NotifyThread notifyThread = new NotifyThread(paramMap, retUrl, 3);
        Thread th = new Thread(notifyThread);
        th.start();
    }

    @Override
    public Page<PaymentRecordVo> list(PaymentRecordQo qo){
        Page p = new Page(qo.getPage(), qo.getLimit());
        return p.setRecords(tempOrderMapper.list(p, qo));
    }
}

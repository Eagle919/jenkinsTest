package com.caih.cloud.iscs.charge.service;

import com.caih.cloud.iscs.charge.model.entity.EOrder;

public interface WechatPayService {


    Boolean refund(String outTradeNo, String totalFee);

    /**
     * 微信H5 支付
     * 注意：必须再web页面中发起支付且域名已添加到开发配置中
     */
    String h5pay(EOrder order, String desc, int money, String spbillCreateIp);

    //根据订单编号查询订单
    Boolean orderquery(String orderNo);
}

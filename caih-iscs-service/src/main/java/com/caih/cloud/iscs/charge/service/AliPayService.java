package com.caih.cloud.iscs.charge.service;

public interface AliPayService {


    Boolean refund(String orderNo, String refund);

    Boolean orderQuery(String orderNo);

    Boolean refundQuery(String orderNo);
}

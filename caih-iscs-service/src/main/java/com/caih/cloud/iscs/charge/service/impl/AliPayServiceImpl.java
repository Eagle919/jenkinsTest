package com.caih.cloud.iscs.charge.service.impl;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.caih.cloud.iscs.charge.constants.AliPayConstants;
import com.caih.cloud.iscs.charge.service.AliPayService;
import com.caih.cloud.iscs.charge.utils.EAliPayUtils;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import com.github.pig.common.util.sherry.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.UUID;

@Slf4j
@Service
public class AliPayServiceImpl implements AliPayService {


    @Override
    public Boolean refund(String outTradeNo, String refund) {

        log.info("订单号【" + outTradeNo + "】支付宝退款【" + refund + "】元");

        // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
        String refundReason = "智慧社区-充电桩剩余时间退款";

        AlipayClient client = new DefaultAlipayClient(
                AliPayConstants.URL, AliPayConstants.APP_ID,
                AliPayConstants.PRIVATE_KEY, AliPayConstants.FORMAT, AliPayConstants.CHARSET,
                AliPayConstants.PUBLIC_KEY_ALI, AliPayConstants.SIGNTYPE);

        AlipayTradeRefundModel rm = new AlipayTradeRefundModel();
        rm.setOutTradeNo(outTradeNo);
        rm.setOutRequestNo(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9).toUpperCase());
        rm.setRefundAmount(refund);
        rm.setRefundReason(refundReason);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizModel(rm);

        try {
            AlipayTradeRefundResponse response = client.execute(request);
            log.info("支付宝退款响应状态response.isSuccess()={}", response.isSuccess());

            String fundChange = response.getFundChange();

            if ("Y".equals(fundChange)) {
                log.info("支付宝退款成功");
                return true;
            } else {
                String subMsg = response.getSubMsg();
                log.info("支付宝退款错误信息 subMsg = {}", subMsg);
                return false;

            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("支付宝退款异常");
            return false;
        }


    }

    @Override
    public Boolean orderQuery(String orderNo) {


        log.info("订单号【" + orderNo + "】发起支付宝支付查询");

        boolean result;

        AlipayClient client = new DefaultAlipayClient(
                AliPayConstants.URL, AliPayConstants.APP_ID,
                AliPayConstants.PRIVATE_KEY, AliPayConstants.FORMAT, AliPayConstants.CHARSET,
                AliPayConstants.PUBLIC_KEY_ALI, AliPayConstants.SIGNTYPE);

        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        model.setOutTradeNo(orderNo);


        alipay_request.setBizModel(model);

        try {
            AlipayTradeQueryResponse alipay_response = client.execute(alipay_request);
            log.info("订单号【" + orderNo + "】发起支付宝支付查询结果【" + alipay_request + "】");
            result = alipay_response.isSuccess();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            result = false;
        }

        log.info("订单号【" + orderNo + "】发起支付宝支付查询是否成功【" + result + "】");
        return result;
    }

    @Override
    public Boolean refundQuery(String orderNo) {
        AlipayClient alipayClient = new DefaultAlipayClient(AliPayConstants.URL, AliPayConstants.APP_ID,
                AliPayConstants.PRIVATE_KEY, AliPayConstants.FORMAT, AliPayConstants.CHARSET,
                AliPayConstants.PUBLIC_KEY_ALI, AliPayConstants.SIGNTYPE);//test ali public key

        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();

        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(orderNo);
        model.setOutRequestNo(UUID.randomUUID().toString().replaceAll("-", ""));

        request.setBizModel(model);

        AlipayTradeFastpayRefundQueryResponse response;
        try {
            response = alipayClient.execute(request);
            log.info("退款查询返回结果【" + response.isSuccess() + "】");
            if (response.isSuccess()) {
                return true;
            } else {
                return false;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("退款查询返回结果【发生异常】");
            return false;
        }

    }

}

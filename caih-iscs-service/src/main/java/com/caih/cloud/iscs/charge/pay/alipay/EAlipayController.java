package com.caih.cloud.iscs.charge.pay.alipay;

import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caih.cloud.iscs.charge.constants.AliPayConstants;
import com.caih.cloud.iscs.charge.model.entity.EAliSuccessRecord;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import com.caih.cloud.iscs.charge.scoket.DeviceHandler;
import com.caih.cloud.iscs.charge.service.*;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import com.caih.cloud.iscs.common.DateUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/alipay")
public class EAlipayController {

    @Resource
    private EAliSuccessRecordService aliService;

    @Resource
    private ECommunityService communityService;

    @Resource
    private ESocketService socketService;

    @Resource
    private EOrderService orderService;

    @Resource
    private SmartCommunityService smartCommunityService;

    /**
     * 支付
     */
    @PostMapping("/pay/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) {

        //将异步通知中收到的所有参数都存放到map中
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Object o : requestParams.keySet()) {
            String name = (String) o;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++)
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        log.info("【支付宝异步通知参数】" + params);

        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, AliPayConstants.PUBLIC_KEY_ALI, AliPayConstants.CHARSET, AliPayConstants.SIGNTYPE);
            log.info("支付宝回调签名验证是否通过【" + flag + "】");

            String outTradeNo = params.get("out_trade_no");
            if (outTradeNo == null) {
                log.error("【outTradeNo】 == null");
                return;
            }
            log.info("call back from alipay, 【outTradeNo】 = " + outTradeNo);

            //退款通知回调相关开始
            if (flag && params.getOrDefault("auth_app_id", "").equals(AliPayConstants.APP_ID)
                    && params.containsKey("refund_fee")) {
                //更新退款数据相关 todo
                //退款通知回调相关结束
            } else if (flag && params.getOrDefault("trade_status", "").equals("TRADE_SUCCESS")
                    && params.containsKey("total_amount")
                    && params.getOrDefault("app_id", "").equals(AliPayConstants.APP_ID)) {
                synchronized (this) {
                    //todo 成功后写入数据到ZAliSuccessRecord表里
                    log.info("==========================支付成功后回调==========================");

                    QueryWrapper<EAliSuccessRecord> aliqw = new QueryWrapper<>();
                    aliqw.eq("out_trade_no", outTradeNo);
                    List<EAliSuccessRecord> successRecords = aliService.list(aliqw);

                    if (successRecords.isEmpty()) {
                        log.info("add a new record outtradeno = " + outTradeNo);
                        EAliSuccessRecord ali = new EAliSuccessRecord();
                        ali.setTotalAmount(params.get("total_amount"));
                        ali.setTradeNo(params.get("trade_no"));
                        ali.setOutTradeNo(params.get("out_trade_no"));
                        aliService.save(ali);
                    }
                }

                //处理支付成功后的业务
                //订单相关字段更新
                QueryWrapper<EOrder> orderQueryWrapper = new QueryWrapper<>();
                orderQueryWrapper.eq("order_no", outTradeNo);
                EOrder order = orderService.getOne(orderQueryWrapper);
                log.info("编号为【" + outTradeNo + "】的订单是【" + order + "】");

                if (order == null) return;

                order.setUpdateTime(new Date());
                order.setStatus(1);
                order.setPayStatus(2);

                ECommunity community = communityService.getById(order.getCommunityId());
                log.info("ID为【" + order.getCommunityId() + "】的社区是【" + community + "】");

                if (community == null) return;

                ESocket socket = socketService.getById(order.getSocketId());
                log.info("ID为【" + order.getSocketId() + "】的插座是【" + socket + "】");

                if (socket == null) return;

                //插座相关字段更新
                socket.setStatus(0);//使用中
                socket.setUpdateTime(new Date());
                socket.setStartTime(new Date());
                socket.setEndTime(TimeUtils.addDateHour(socket.getStartTime(), Integer.parseInt(order.getChargeTime())));


                //todo 当调用充电接口没有异常时 更新
                socketService.updateById(socket);
                orderService.updateById(order);

                // 调用充电接口
                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    String startTime = sdf.format(socket.getStartTime());
//                    String endTime = sdf.format(socket.getEndTime());
//                    int times = DateUtils.timeDifference(startTime, endTime);
                    String chargeTime = order.getChargeTime();
                    int time = Integer.parseInt(chargeTime) * 60;

                    smartCommunityService.startCharge(socket.getDeviceNo(), socket.getPortNo(), time);
                    /*socketService.updateById(socket);
                    orderService.updateById(order);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                log.error("alipay sign false");//退款的是使用支付宝公钥的验签，默认使用的是初始化客户端时候的公钥，需要把初始化客户端的公钥修改为支付宝的公钥
            }
        } catch (Exception e) {
            log.error("alipay Exception", e);
        }

        try (BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            out.write("success".getBytes());
        } catch (Exception e) {
            log.error("alipay write fail!!!", e);
        }

    }

    public static void main(String[] args) {
        String startTime = "2020-02-27 11:50:35";
        String endTime = " 2020-02-27 15:50:35";

        int times = DateUtils.timeDifference(endTime, startTime);
        System.out.println("times = " + times);

    }


}

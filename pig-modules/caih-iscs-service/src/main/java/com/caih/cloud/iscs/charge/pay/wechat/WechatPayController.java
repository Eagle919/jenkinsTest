package com.caih.cloud.iscs.charge.pay.wechat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caih.cloud.iscs.charge.aasecretkey.MyWXPayConfig;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import com.caih.cloud.iscs.charge.model.entity.EWxSuccessRecord;
import com.caih.cloud.iscs.charge.model.vo.EHomeVo;
import com.caih.cloud.iscs.charge.scoket.DeviceHandler;
import com.caih.cloud.iscs.charge.service.*;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import com.caih.cloud.iscs.common.CommonResult;
import com.caih.cloud.iscs.common.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

//  文档地址 https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
//  https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=4_3

@Slf4j
@RestController
@RequestMapping("/wechat")
public class WechatPayController {

//    1,调起支付请求和 回调地址 写在一个目录下
//2,域名要和后台配置的域名要一样

    @Resource
    private MyWXPayConfig myWXPayConfig;

    @Resource
    private EWxSuccessRecordService wxSuccessRecordService;

    @Resource
    private ESocketService socketService;

    @Resource
    private EOrderService orderService;

    @Resource
    private ECommunityService communityService;

    @Resource
    private SmartCommunityService smartCommunityService;


    @RequestMapping("/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) {
        try (
                InputStream inputStream = request.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            StringBuilder buffer = new StringBuilder();
            String s;
            while ((s = in.readLine()) != null)
                buffer.append(s);
            Map<String, String> params = myWXPayConfig.parseWxXML(buffer.toString());
            log.info("【微信异步通知参数】" + params);

            String outTradeNo = params.get("out_trade_no");
            if (outTradeNo == null) {
                log.error("outTradeNo == null");
            }
            log.info("call back from wx!!! outTradeNo = " + outTradeNo);
            String sign = params.get("sign");
            if (sign == null) {
                log.info("sign = null");
                return;
            }
            params.remove("sign");
            String myCalculate = myWXPayConfig.createSign(params);
            if (sign.equals(myCalculate)
                    && params.containsKey("out_trade_no")
                    && params.containsKey("transaction_id")
                    && params.containsKey("total_fee")) { // 是微信官方返回的数据
                //微信支付成功后的回调
                synchronized (this) {
                    log.info("new pay record from wx outTradeNo = " + outTradeNo);

                    QueryWrapper<EWxSuccessRecord> wrapper = new QueryWrapper<>();
                    wrapper.eq("out_trade_no", outTradeNo);
                    List<EWxSuccessRecord> list = wxSuccessRecordService.list(wrapper);

                    if (list.isEmpty()) {
                        EWxSuccessRecord record = new EWxSuccessRecord();
                        record.setOutTradeNo(params.get("out_trade_no"));
                        record.setTotalFee(params.get("total_fee"));
                        record.setTransactionId(params.get("transaction_id"));
                        wxSuccessRecordService.save(record);
                    }

                    //处理支付成功后的业务
//                    //订单相关字段更新
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

//                    插座相关字段更新
                    socket.setStatus(0);//使用中
                    socket.setUpdateTime(new Date());
                    socket.setStartTime(new Date());
                    socket.setEndTime(TimeUtils.addDateHour(socket.getStartTime(), Integer.parseInt(order.getChargeTime())));

                    //todo 当调用充电接口没有异常时再更新数据
                    socketService.updateById(socket);
                    orderService.updateById(order);

                    // 调用充电接口
                    try {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        String startTime = sdf.format(socket.getStartTime());
//                        String endTime = sdf.format(socket.getEndTime());
//                        int times = DateUtils.timeDifference(startTime, endTime);

                        String chargeTime = order.getChargeTime();
                        int time = Integer.parseInt(chargeTime) * 60;
                        smartCommunityService.startCharge(socket.getDeviceNo(), socket.getPortNo(), time);
                        /*socketService.updateById(socket);
                        orderService.updateById(order);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } else { // 有人恶意造假
                log.error("wx callback error sign mismatch");
            }
            out.write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>".getBytes());
        } catch (Exception e) {
            log.error("wx callback error occurs!!!", e);

        }

    }


}

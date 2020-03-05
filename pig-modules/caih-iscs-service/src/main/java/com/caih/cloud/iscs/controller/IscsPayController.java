package com.caih.cloud.iscs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.caih.cloud.iscs.common.*;
import com.caih.cloud.iscs.model.dto.IscsOrderInfoDto;
import com.caih.cloud.iscs.model.dto.TempOrderQrcodeDto;
import com.caih.cloud.iscs.model.entity.*;
import com.caih.cloud.iscs.model.qo.PaymentRecordQo;
import com.caih.cloud.iscs.service.IscsPayService;
import com.caih.cloud.iscs.service.ScStoreService;
import com.caih.cloud.iscs.service.ScTurnoverService;
import com.github.pig.common.util.Base64Util;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.caih.cloud.iscs.common.Constants.*;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Slf4j
@Api(value = "/pay", tags = "订单支付")
@RestController
@RequestMapping("/pay")
public class IscsPayController extends BaseController {

    @Value("${allinpay.secret}")
    private String secret;

    @Value("${allinpay.cusid}")
    private String cusid;

    @Value("${allinpay.appid}")
    private String appid;

    @Value("${allinpay.appkey}")
    private String appkey;

    @Value("${allinpay.request-url}")
    private String requestUrl;

    @Value("${allinpay.ret-url}")
    private String retUrl;

    @Value("${allinpay.notify-url}")
    private String notifyUrl;

    @Value("${order.secret}")
    private String orderSecret;

    @Autowired
    private IscsPayService iscsPayService;
    @Autowired
    private ScTurnoverService scTurnoverService;
    @Autowired
    private ScStoreService scStoreService;
    @Autowired
    private DistributeLock distributeLock;


    @ApiOperation(value = "订单申请支付接口", httpMethod = "POST")
    @PostMapping("/payment")
    @Transactional
    public R payment(@Valid @RequestBody IscsOrderInfoDto orderInfoDto, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()){
                return new R(R.FAIL,SPLIT_EMPTY_STRING,bindingResult.getFieldError().getDefaultMessage());
            }

            //验签
            Map treeMap = new TreeMap();
            treeMap.put("orderNo", orderInfoDto.getOrderNo());
            treeMap.put("storeNo", orderInfoDto.getStoreNo());
            treeMap.put("buyer", orderInfoDto.getBuyer());
            treeMap.put("buyerPhone", orderInfoDto.getBuyerPhone());
            treeMap.put("sign", orderInfoDto.getSign());
            Boolean signFlag = AllInPayUtil.validsign(treeMap, orderSecret);
            if(!signFlag){
                return new R(R.FAIL, "0", "数据验签失败");
            }

            //判断平台是否有录入订单中的商户
            QueryWrapper<StoreInfo> siqw = new QueryWrapper<>();
            siqw.eq("store_no", orderInfoDto.getStoreNo());
            StoreInfo si = scStoreService.getBaseMapper().selectOne(siqw);
            if (si == null) {
                return new R(R.FAIL, "-1", "平台没有录入订单中的商户");
            }

            //判断是否已录入订单数据
            QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
            String orderNo = orderInfoDto.getOrderNo();
            qw.eq("order_no", orderNo);
            int count = iscsPayService.getBaseMapper().selectCount(qw);
            if (count > 0) {
                qw.eq("order_state", "1");
                int isPayFlag = iscsPayService.getBaseMapper().selectCount(qw);
                if(isPayFlag > 0){
                    return new R(R.FAIL, "1", "订单已支付");
                }else{
                    TempOrderQrcode qr = new TempOrderQrcode();
                    QueryWrapper<TempOrderQrcode> qrqw = new QueryWrapper<>();
                    qrqw.eq("order_no", orderNo);
                    qr = qr.selectOne(qrqw);
                    return new R(R.SUCCESS, qr.getValidKey(), "订单已录入，请支付");
                }
            }

            //订单信息录入
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(orderInfoDto, orderInfo);
            orderInfo.setOrderTime(new Date());//订单创建时间
            orderInfo.setOrderState(0);//待付款状态


            //订单总金额(分)
            double totalAmt = orderInfoDto.getPostage().doubleValue() * 100;

            //订单商品信息录入
            List<OrderGoodsInfo> goodsList = orderInfoDto.getGoodsList();
            for (OrderGoodsInfo orderGoodsInfo : goodsList){
                orderGoodsInfo.setOrderNo(orderInfoDto.getOrderNo());
                totalAmt = totalAmt + orderGoodsInfo.getGoodsPrice().doubleValue() * 100;
                orderGoodsInfo.insert();
            }

            //生成分账信息
            /*
            分账信息[格式:(cusid:type:amount;cusid:type:amount…);其中:
            cusid:接收分账的通联商户号;
            type分账类型(01：按金额  02：按比率);
            如果分账类型为02，则分账比率为0.5表示50%。如果分账类型为01，则分账金额以元为单位表示]
            */
            String asinfo = "55161105331X1DV:02:0.1";
            //判断：分账最小金额为1分
            if((totalAmt*0.1) < 1){
                asinfo = "55161105331X1DV:01:0.01";
            }
            orderInfo.setAsinfo(asinfo);
            orderInfo.insert();

            //validKey，用UUID生成
            String validKey = UUID.randomUUID().toString().replaceAll("-", "");
            //validKey存入临时表
            TempOrderQrcode orderQrcode = new TempOrderQrcode();
            orderQrcode.setOrderNo(orderInfoDto.getOrderNo());
            orderQrcode.setRetUrl(orderInfoDto.getReturnUrl());
            orderQrcode.setValidKey(validKey);
            orderQrcode.insert();
            //返回validKey
            return new R(R.SUCCESS, validKey, "订单申请成功，请支付");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new R(R.FAIL, e.getMessage(), "操作失败，请联系系统管理员");
        }
    }

    @ApiOperation(value = "支付页面获取接口", httpMethod = "GET")
    @GetMapping("/payPage")
    public R payPage(@RequestParam("validKey")final String validKey) {
        try {
            TempOrderQrcodeDto orderQrcodeDto = iscsPayService.queryByValidKey(validKey);
            if (orderQrcodeDto == null) {
                return new R(R.FAIL, "-1", "支付校验码不正确");
            }

            //判断订单数据
            QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
            String orderNo = orderQrcodeDto.getOrderNo();
            qw.eq("order_no", orderNo);
            OrderInfo orderInfo = iscsPayService.getBaseMapper().selectOne(qw);
            if (orderInfo == null) {
                return new R(R.FAIL, "0", "无订单数据");
            }else if("1".equals(orderInfo.getOrderState().toString())){
                return new R(R.FAIL, "1", "订单已支付");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 拼接支付参数
            StringBuffer payUrl = new StringBuffer(requestUrl);
            System.out.println("requestUrl:-------------------->"+requestUrl);

            TreeMap<String,String> treeMap = new TreeMap<>();
            treeMap.put("cusid",orderQrcodeDto.getCusid());
            payUrl.append("?cusid=" + orderQrcodeDto.getCusid());
            System.out.println("cusid:-------------------->"+orderQrcodeDto.getCusid());

            treeMap.put("appid", orderQrcodeDto.getAppid());
            payUrl.append("&appid=" + orderQrcodeDto.getAppid());
            System.out.println("appid:-------------------->"+orderQrcodeDto.getAppid());

            treeMap.put("version", "12");
            payUrl.append("&version=12");

            BigDecimal trxamt = orderQrcodeDto.getOrderTotalAmt().setScale(2, RoundingMode.HALF_UP);
            treeMap.put("trxamt", trxamt.toString().replace(".",""));
            payUrl.append("&trxamt=" + trxamt.toString().replace(".",""));

            treeMap.put("reqsn",orderQrcodeDto.getOrderNo());
            payUrl.append("&reqsn=" + orderQrcodeDto.getOrderNo());

            treeMap.put("charset","UTF-8");
            payUrl.append("&charset=UTF-8");

            treeMap.put("returl", retUrl);
            payUrl.append("&returl=" + retUrl);

            treeMap.put("notify_url", notifyUrl);
            payUrl.append("&notify_url=" + notifyUrl);

            treeMap.put("body","payDetail");
            payUrl.append("&body=payDetail");

            treeMap.put("remark","buyer:" + orderQrcodeDto.getBuyer());
            payUrl.append("&remark=buyer:" + orderQrcodeDto.getBuyer());

            String randomstr = SybUtil.getValidatecode(8);
            treeMap.put("randomstr", randomstr);
            payUrl.append("&randomstr=" + randomstr);

            treeMap.put("asinfo", orderInfo.getAsinfo());
            payUrl.append("&asinfo=" + orderInfo.getAsinfo());
            System.out.println("asinfo:-------------------->"+orderInfo.getAsinfo());

            String sign = SybUtil.sign(treeMap,orderQrcodeDto.getMd5key());
            payUrl.append("&sign="+sign);
            System.out.println("sign:-------------------->"+sign);
            System.out.println("payUrl:-------------------->"+payUrl);
            //payUrl.append("&sign=" + AllInPayUtil.signData(treeMap, secret));

            baos = QCodeUtil.createZxingqrCode(payUrl.toString(),baos);
            orderQrcodeDto.setQrCodeStr("data:image/png;base64, " + Base64Util.byte2Base64String(baos.toByteArray()));

            return new R(R.SUCCESS, orderQrcodeDto, "获取成功");
        }catch (Exception e){
            log.error(e.getMessage());
            return new R(R.FAIL, e.getMessage(), "操作失败，请联系系统管理员");
        }
    }

    @ApiOperation(value = "支付结果通知接口", httpMethod = "POST")
    @PostMapping("/payNotify")
    @Transactional
    public String payNotify(@RequestParam Map<String, String> paramMap) {
        try {
            System.out.println("----------------------------payNotify----------------------------");
            TreeMap<String,String> param = new TreeMap<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                param.put(entry.getKey(), entry.getValue());
                log.info(" key:"+entry.getKey()+" vlaue:"+entry.getValue());
                System.out.println(" key:"+entry.getKey()+" vlaue:"+entry.getValue());
            }
            System.out.println("----------------------------payNotify----------------------------");

            //订单号
            String orderNo = paramMap.get("cusorderid");
            //交易单号
            String dealNo = paramMap.get("trxid");
            //交易状态
            String trxstatus = paramMap.get("trxstatus");
            //查询订单商户相关信息
            TempOrderQrcodeDto orderQrcodeDto = iscsPayService.queryByOrderNo(orderNo);

            //验签
            //Boolean flag = AllInPayUtil.validsign(paramMap, secret);
            Boolean flag =  SybUtil.validSign(param, orderQrcodeDto.getMd5key());
            if (!flag){
                log.info("支付结果通知接口:验签失败");
                return "fail";
            }
            //查询订单信息
            QueryWrapper<OrderInfo> qwoi = new QueryWrapper<>();
            qwoi.eq("order_no", orderNo);
            OrderInfo orderInfo = iscsPayService.getBaseMapper().selectOne(qwoi);
            //查询商户信息
            QueryWrapper<StoreInfo> qwsi = new QueryWrapper<>();
            qwsi.eq("store_no",orderInfo.getStoreNo());
            StoreInfo storeInfo = scStoreService.getBaseMapper().selectOne(qwsi);

            //订单状态
            String orderState = "1";
            //交易成功
            if("0000".equals(paramMap.get("trxstatus"))){
                //加分布式锁
                String lockKey = LOCK_PREFIX + storeInfo.getStoreNo();
                Long threadId = Thread.currentThread().getId();
                String lockFlag = distributeLock.lock(lockKey, threadId.toString(), 60000);
                if("OK".equals(lockFlag)){//加锁成功
                    //更新商户的账户余额
                    storeInfo.setAccBalance(storeInfo.getAccBalance().add
                            (orderQrcodeDto.getOrderTotalAmt()));
                    scStoreService.getBaseMapper().updateById(storeInfo);
                    //解锁
                    distributeLock.unlock(lockKey, threadId.toString());
                }else{
                    log.info("该商户正在加锁，无法操作余额");
                    return "fail";
                }

                //付款成功插入流水信息
                TurnoverInfo ti = new TurnoverInfo();
                ti.setDealNo(dealNo);
                ti.setStoreNo(orderInfo.getStoreNo());
                ti.setOrderNo(orderNo);
                ti.setAccBalance(storeInfo.getAccBalance());
                ti.setTurnoverAmt(orderQrcodeDto.getOrderTotalAmt());
                ti.setTurnoverType(1);
                ti.setTurnoverState("1");
                ti.setTurnoverTime(new Date());
                ti.setRemarks("订单："+orderNo+"收入");
                scTurnoverService.getBaseMapper().insert(ti);
            }else{
                orderState = "-1";//交易失败状态
            }

            //通知上游的接口
            String retUrl = orderQrcodeDto.getRetUrl();
            Map treeMap = new TreeMap();
            treeMap.put("orderNo", orderNo);
            treeMap.put("dealNo", dealNo);
            treeMap.put("orderState", orderState);
            treeMap.put("trxstatus", trxstatus);
            iscsPayService.orderResultNotify(treeMap, retUrl);

            //修改订单相关信息
            UpdateWrapper<OrderInfo> uw = new UpdateWrapper<>();
            uw.set("order_state", orderState);
            uw.set("deal_no", dealNo);
            uw.set("pay_time", new Date());
            uw.eq("order_no", orderNo);
            orderInfo.update(uw);

            //更新订单二维码表的信息
            Map<String,Object> map = new HashMap<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (!StringUtils.isEmpty(entry.getValue()) && !entry.getKey().equals("sign")) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            TempOrderQrcode toq = new TempOrderQrcode();
            BeanUtils.copyProperties(orderQrcodeDto, toq);
            IscsUtil.copyMapToObject(map, toq);
            UpdateWrapper<TempOrderQrcode> uwtoq = new UpdateWrapper<>();
            uwtoq.eq("order_no", orderNo);
            toq.update(uwtoq);
            System.out.println("------------payNotify success------------");
            return "success";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "fail";
        }
    }

    @ApiOperation(value = "查询订单状态码", httpMethod = "GET")
    @GetMapping("/getOrderState")
    public R getOrderState(@RequestParam("validKey")final String validKey) {
        try {
            TempOrderQrcode qr = new TempOrderQrcode();
            QueryWrapper<TempOrderQrcode> qrqw = new QueryWrapper<>();
            qrqw.eq("valid_key", validKey);
            qr = qr.selectOne(qrqw);

            OrderInfo order = new OrderInfo();
            QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
            qw.eq("order_no", qr.getOrderNo());
            order = order.selectOne(qw);
            int state = order.getOrderState();

            return new R(R.SUCCESS, order.getOrderState(), "查询成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return new R(R.FAIL, -2, "操作失败，请联系系统管理员");
        }
    }

    @ApiOperation(value = "塔西回调接口模拟", httpMethod = "GET")
    @GetMapping("/getTest")
    public String getTest(@RequestParam Map<String, String> paramMap){
        System.out.println("------------------------------in------------------------------");
        for(Map.Entry<String, String> entry : paramMap.entrySet()){
            System.out.println("key:"+entry.getKey()+" value:"+paramMap.get(entry.getKey()));
        }
        return "success";
    }

    @ApiOperation(value = "获取Sign(测试使用)", httpMethod = "GET")
    @GetMapping("/getSign")
    public String getSign(@RequestParam Map<String, String> paramMap){
        return AllInPayUtil.signData(paramMap, orderSecret);
    }

    @ApiOperation(value = "获取支付记录", httpMethod = "GET")
    @GetMapping("/list")
    public R list(@Valid PaymentRecordQo qo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new R(R.FAIL,SPLIT_EMPTY_STRING,bindingResult.getFieldError().getDefaultMessage());
        }
        return new R(R.SUCCESS, iscsPayService.list(qo), "查询成功");
    }
}

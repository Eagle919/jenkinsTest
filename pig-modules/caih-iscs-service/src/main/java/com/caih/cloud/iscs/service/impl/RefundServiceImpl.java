package com.caih.cloud.iscs.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.caih.cloud.iscs.common.*;
import com.caih.cloud.iscs.mapper.*;
import com.caih.cloud.iscs.model.dto.RefundDto;
import com.caih.cloud.iscs.model.dto.TXRefundDto;
import com.caih.cloud.iscs.model.entity.*;
import com.caih.cloud.iscs.model.vo.GoodInfoVo;
import com.caih.cloud.iscs.model.vo.RefundRespondFail;
import com.caih.cloud.iscs.model.vo.RefundRespondSuccess;
import com.caih.cloud.iscs.service.RefundService;
import com.github.pig.common.util.sherry.HttpWrapper;
import com.github.pig.common.util.sherry.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.caih.cloud.iscs.common.Constants.LOCK_PREFIX;

@Service
public class RefundServiceImpl implements RefundService {

    @Value("${order.secret}")
    private String orderSecret;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private GoodInfoMapper goodInfoMapper;

    @Resource
    private ScStoreMapper scStoreMapper;


    @Resource
    private RefundOrderInfoMapper refundOrderInfoMapper;

    @Resource
    private DistributeLock distributeLock;

    @Resource
    private ScTurnoverMapper scTurnoverMapper;

    @Override
    @Transactional
    public Pair<String, Object> refund(TXRefundDto dto) {
        Pair<String, Object> pair = new Pair<>();
        RefundRespondFail respondFail = new RefundRespondFail();
        //验签-塔西
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("storeNo", dto.getStoreNo());
        treeMap.put("oldreqsn", dto.getOldreqsn());
        treeMap.put("sign", dto.getSign());
        treeMap.put("trxamt", dto.getTrxamt());
        treeMap.put("reqsn", dto.getReqsn());
        boolean validsign = AllInPayUtil.validsign(treeMap, orderSecret);
        if (!validsign) return fail4tx(pair, respondFail, "提交失败:签名有误");
        //获取商户信息
        QueryWrapper<StoreInfo> siqw = new QueryWrapper<>();
        siqw.eq("store_no", dto.getStoreNo());
        StoreInfo store = scStoreMapper.selectOne(siqw);
        //退款订单号是否唯一
        QueryWrapper<RefundOrderInfo> roqw = new QueryWrapper<>();
        roqw.eq("refund_order_no", dto.getReqsn());
        Integer count = refundOrderInfoMapper.selectCount(roqw);
        if (count > 0) return fail4tx(pair, respondFail, "提交失败:退款订单号已存在");
        //获取订单
        QueryWrapper<OrderInfo> oiqw = new QueryWrapper<>();
        oiqw.eq("order_no", dto.getOldreqsn());
        OrderInfo order = orderMapper.selectOne(oiqw);
        if (order == null) return fail4tx(pair, respondFail, "提交失败:无该订单");
        if (order.getOrderState() == -1 || order.getOrderState() == 0 || order.getOrderState() == 4)
            return fail4tx(pair, respondFail, "提交失败:还未付款");
        if (order.getOrderState() == 8)
            return fail4tx(pair, respondFail, "提交失败:退款已经完成");
        //订单总金额
        List<GoodInfoVo> goodInfoVos = goodInfoMapper.goodInfos(order.getOrderNo());
        BigDecimal totalAmount = new BigDecimal("0");
        for (GoodInfoVo item : goodInfoVos)
            totalAmount = totalAmount.add(new BigDecimal(item.getGoodsPrice()).multiply(new BigDecimal(item.getGoodsNum())));
        //判断退款金额是否小于等于订单金额
        BigDecimal amt = new BigDecimal(dto.getTrxamt());
        amt = amt.multiply(new BigDecimal(100));
        totalAmount = totalAmount.multiply(new BigDecimal(100));
        if (amt.compareTo(totalAmount) > 0) return fail4tx(pair, respondFail, "提交失败:退款金额大于订单总额");
        boolean isToday = false;
        try {
            //判断订单时间是否超过24：00
            Date orderTime = order.getOrderTime();
            String format = "HH:mm:ss";
            Date endTime = new SimpleDateFormat(format).parse("23:59:59");
            isToday = DateUtils.isEffectiveDate(orderTime, orderTime, endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (isToday) {
            //获取流水
            QueryWrapper<TurnoverInfo> tqw = new QueryWrapper<>();
            tqw.eq("order_no", dto.getOldreqsn());
            tqw.eq("turnover_type", "1");
            TurnoverInfo ti = scTurnoverMapper.selectOne(tqw);
            if (ti == null) return fail4tx(pair, respondFail, "提交失败:交易流水未找到");
            //元 -》 分
            BigDecimal bd = new BigDecimal(dto.getTrxamt());
            bd = bd.multiply(new BigDecimal(100));
            // #.00 表示两位小数 #.0000四位小数
            DecimalFormat df2 = new DecimalFormat("#");
            String m = df2.format(bd);
            //封装通联数据参数
            RefundDto refundDto = new RefundDto();
            refundDto.setCusid(store.getCusid());
            refundDto.setAppid(store.getAppid());
            refundDto.setTrxamt(m);
            refundDto.setReqsn(dto.getReqsn());
            refundDto.setOldreqsn(dto.getOldreqsn());
            refundDto.setOldtrxid(ti.getDealNo());
            refundDto.setRandomstr(StringUtils.getRandomCharAndNumr(8));
            //封装获取签名参数
            TreeMap<String, String> signMap = new TreeMap<>();
            signMap.put("cusid", refundDto.getCusid());
            signMap.put("appid", refundDto.getAppid());
            signMap.put("trxamt", m);
            signMap.put("reqsn", refundDto.getReqsn());
            signMap.put("oldreqsn", refundDto.getOldreqsn());
            signMap.put("oldtrxid", refundDto.getOldtrxid());
            signMap.put("randomstr", refundDto.getRandomstr());
            String sign = "";
            try {
                sign = SybUtil.sign(signMap, store.getMd5key());
            } catch (Exception e) {
                e.printStackTrace();
            }
            refundDto.setSign(sign);
            //获取通联接口数据
            RefundRespondSuccess rrs = getTlDataByUrl(refundDto);
            String returnCode = "";
            if (rrs != null) returnCode = rrs.getRetcode();
            // 插入退款订单信息表数据
            RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
            refundOrderInfo.setRefundTime(DateUtils.getStringDate());
            // 插入交易流水表数据数据
            TurnoverInfo turnoverInfo = new TurnoverInfo();
            turnoverInfo.setTurnoverTime(new Date());
            if (Constants.DEAL_SUCCESS.equals(returnCode)) {
                //returnCode是3开头的是退款失败
                String status = rrs.getTrxstatus();
                String returnStatus = status.substring(0, 1);
                if (returnStatus.equals(Constants.DEAL_FAIL_3) || !Constants.DEAL_SUCCESS_0000.equals(rrs.getTrxstatus())) {
                    //修改订单状态
                    order.setOrderState(9);//退款失败
                    orderMapper.updateById(order);
                    //封装退款订单字段
                    packgeFee(rrs, refundOrderInfo);
                    refundOrderInfo.setErrmsg(rrs.getErrmsg());
                    refundOrderInfo.setOrderNo(dto.getOldreqsn());
                    refundOrderInfo.setRefundOrderNo(rrs.getReqsn());
                    refundOrderInfo.setTrxstatus(rrs.getTrxstatus());
                    refundOrderInfo.setTrxcode(rrs.getTrxcode());
                    refundOrderInfoMapper.insert(refundOrderInfo);
                    //封装流水字段
                    turnoverInfo.setDealNo(rrs.getTrxid());
                    turnoverInfo.setOrderNo(dto.getOldreqsn());
                    turnoverInfo.setTurnoverState("0");//失败
                    turnoverInfo.setStoreNo(store.getStoreNo());
                    turnoverInfo.setTurnoverAmt(new BigDecimal(dto.getTrxamt()));
                    turnoverInfo.setAccBalance(store.getAccBalance());
                    turnoverInfo.setTurnoverTime(new Date());
                    turnoverInfo.setTurnoverType(2);//退款
                    scTurnoverMapper.insert(turnoverInfo);
                    //返回前端提示信息
                    return fail4tl(pair, respondFail, rrs.getErrmsg());
                } else if (Constants.DEAL_SUCCESS_0000.equals(rrs.getTrxstatus())) {
                    //交易成功
                    //修改订单状态
                    order.setOrderState(8);//退款成功
                    orderMapper.updateById(order);
                    //封装退款订单字段
                    refundOrderInfo.setErrmsg(rrs.getErrmsg());
                    //分 -> 元
                    packgeFee(rrs, refundOrderInfo);
                    refundOrderInfo.setRefundOrderNo(rrs.getReqsn());
                    refundOrderInfo.setOrderNo(dto.getOldreqsn());
                    refundOrderInfo.setTrxstatus(rrs.getTrxstatus());
                    refundOrderInfo.setTrxcode(rrs.getTrxcode());
                    refundOrderInfo.setFintime(rrs.getFintime());
                    //插入退款订单数据
                    refundOrderInfoMapper.insert(refundOrderInfo);
                    //修改账户金额
                    store.setAccBalance(store.getAccBalance().subtract(new BigDecimal(dto.getTrxamt()))); //减钱
                    //iscs_turnover_info
                    turnoverInfo.setDealNo(rrs.getTrxid());
                    turnoverInfo.setOrderNo(dto.getOldreqsn());
                    turnoverInfo.setTurnoverState("1");//成功
                    turnoverInfo.setTurnoverAmt(new BigDecimal(dto.getTrxamt()));
                    turnoverInfo.setStoreNo(store.getStoreNo());
                    turnoverInfo.setAccBalance(store.getAccBalance());
                    turnoverInfo.setTurnoverType(2); //退款
                    turnoverInfo.setTurnoverTime(new Date());
                    //插入流水数据
                    scTurnoverMapper.insert(turnoverInfo);
                    String lockKey = LOCK_PREFIX + dto.getStoreNo();
                    long threadId = Thread.currentThread().getId();
                    String lockFlag = distributeLock.lock(lockKey, Long.toString(threadId), 60000);
                    if ("OK".equals(lockFlag)) {
                        //加锁成功
                        //修改账户可提现金额
                        scStoreMapper.updateById(store);
                        //解锁
                        distributeLock.unlock(lockKey, Long.toString(threadId));
                        pair.setFirst(Constants.DEAL_SUCCESS);
                        pair.setSecond(rrs);
                        return pair;
                    } else return fail4tl(pair, respondFail, "该商户正在加锁，无法操作余额");
                } else {
                    //返回前端提示信息
                    return fail4tl(pair, respondFail, rrs.getErrmsg());
                }

            }
            if (Constants.DEAL_FAIL.equals(returnCode) || Constants.DEAL_PARAMERR.equals(returnCode)
                    || Constants.DEAL_SIGNAUTHERR.equals(returnCode) || Constants.DEAL_SYSTEMERR.equals(returnCode)) {
                //修改订单状态
                order.setOrderState(9);//退款失败
                orderMapper.updateById(order);
                return fail4tl(pair, respondFail, "提交成功:" + rrs.getRetmsg());
            }
        } else return fail4tx(pair, respondFail, "提交失败:退款时间已经超过了下单当天的23点59分59秒");

        return pair;
    }

    private void packgeFee(RefundRespondSuccess rrs, RefundOrderInfo refundOrderInfo) {
        if (rrs.getFee() != null) {
            BigDecimal fee = new BigDecimal(rrs.getFee());
            fee = fee.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            refundOrderInfo.setFee(fee);
        } else {
            refundOrderInfo.setFee(new BigDecimal(0));
        }
    }

    private Pair<String, Object> fail4tx(Pair<String, Object> pair, RefundRespondFail respondFail, String msg) {
        respondFail.setRetcode(Constants.DEAL_FAIL);
        respondFail.setRetmsg(msg);
        pair.setFirst(Constants.DEAL_FAIL_TX);
        pair.setSecond(respondFail);
        return pair;
    }

    private Pair<String, Object> fail4tl(Pair<String, Object> pair, RefundRespondFail respondFail, String msg) {
        respondFail.setRetcode(Constants.DEAL_FAIL);
        respondFail.setRetmsg(msg);
        pair.setFirst(Constants.DEAL_FAIL);
        pair.setSecond(respondFail);
        return pair;
    }

    private RefundRespondSuccess getTlDataByUrl(RefundDto dto) {
        //发送请求获取退款数据
        String refundUrl = Constants.DEAL_APIURL_PRD_REFUND;
        // 拼接参数
        refundUrl += "?cusid=" + dto.getCusid();
        refundUrl += "&appid=" + dto.getAppid();
        refundUrl += "&trxamt=" + dto.getTrxamt();
        refundUrl += "&oldtrxid=" + dto.getOldtrxid();
        refundUrl += "&randomstr=" + dto.getRandomstr();
        refundUrl += "&sign=" + dto.getSign();
        refundUrl += "&reqsn=" + dto.getReqsn();
        refundUrl += "&oldreqsn=" + dto.getOldreqsn();
        String data = HttpWrapper.doPost(refundUrl, null);
        return JSON.parseObject(data, RefundRespondSuccess.class);
    }


}

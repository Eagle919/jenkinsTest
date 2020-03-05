package com.caih.cloud.iscs.charge.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.caih.cloud.iscs.charge.constants.AliPayConstants;
import com.caih.cloud.iscs.charge.em.PayWay;
import com.caih.cloud.iscs.charge.model.dto.EHomeDto;
import com.caih.cloud.iscs.charge.model.entity.*;
import com.caih.cloud.iscs.charge.model.pojo.CurrentUser;
import com.caih.cloud.iscs.charge.model.pojo.Geographic;
import com.caih.cloud.iscs.charge.model.pojo.Notices;
import com.caih.cloud.iscs.charge.model.pojo.Tags;
import com.caih.cloud.iscs.charge.model.vo.EHomeVo;
import com.caih.cloud.iscs.charge.model.vo.EOrderVo;
import com.caih.cloud.iscs.charge.model.vo.ESocketVo;
import com.caih.cloud.iscs.charge.scoket.DeviceStatus;
import com.caih.cloud.iscs.charge.service.*;
import com.caih.cloud.iscs.charge.utils.CentToDisplay;
import com.caih.cloud.iscs.charge.utils.EStringUtils;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import com.caih.cloud.iscs.common.CommonResult;
import com.caih.cloud.iscs.common.DateUtils;
import com.caih.cloud.iscs.common.Pair;
import com.github.pig.common.util.Assert;
import com.github.pig.common.util.Query;
import com.github.pig.common.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 前端-主页控制器
 */
@Slf4j
@RestController
@RequestMapping("/home")
public class EHomeController extends BaseController {

    private ECommunityService communityService;

    @Resource
    private EDeviceService deviceService;

    @Resource
    private ESocketService socketService;

    @Resource
    private EOrderService orderService;

    @Resource
    private WechatPayService wechatPayService;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private SmartCommunityService smartCommunityService;


    @Resource
    private ZWxPreRecordService wxPreRecordService;

    @Resource
    private ZAliPreRecordService aliPreRecordService;

    @Resource
    private EMemberService memberService;


    @Autowired
    public EHomeController(ECommunityService service) {
        this.communityService = service;
    }

    // chargeMoney 单位是元
    private static String createAliPayData(String unifiedOrder, BigDecimal chargeMoney) {
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        // 订单名称，必填
        String subject = "智慧社区支付宝支付";
        // 付款金额，必填
        String total_amount = chargeMoney.toString();
        // 商品描述，可空
        // 超时时间 可空
        // 销售产品码 必填
        String product_code = "QUICK_WAP_WAY";

        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(AliPayConstants.URL, AliPayConstants.APP_ID,
                AliPayConstants.PRIVATE_KEY, AliPayConstants.FORMAT, AliPayConstants.CHARSET, AliPayConstants.PUBLIC_KEY_ALI, AliPayConstants.SIGNTYPE);

        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setSubject(subject);
        model.setOutTradeNo(unifiedOrder);
        model.setTotalAmount(total_amount);
        model.setProductCode(product_code);
        model.setBody("智慧社区支付宝支付");
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(AliPayConstants.notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(AliPayConstants.return_url);

        // form表单生产
        String form = null;
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();

        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return form;
    }

    /**
     * 扫码充电-详情
     *
     * @return EHomeVo
     */
    @PostMapping("/checkDevice")
    public CommonResult<EHomeVo> checkDevice(@RequestBody EHomeDto dto) {

        if (dto.getDeviceNo() == null) return CommonResult.success(null, "参数【deviceNo】不能为空");
        if (dto.getAccessToken() == null) return CommonResult.success(null, "参数【accessToken】不能为空");

        EHomeVo vo = new EHomeVo();

        Pair<Object, String> pair = smartCommunityService.checkUser(dto.getAccessToken());
        if (pair.getFirst() == null) {
            vo.setCheckUserMsg("2");
            return CommonResult.unauthorized(vo);
        } else {
            vo.setCheckUserMsg("1");
        }


        //根据设备编号判断是否存在改设备
        QueryWrapper<EDevice> dqw = new QueryWrapper<>();
        dqw.eq("device_no", dto.getDeviceNo());
        Integer deviceCount = deviceService.getBaseMapper().selectCount(dqw);
        if (deviceCount == 0) {
            vo.setCheckDeviceStatus(2);
            vo.setCheckDeviceMsg("编号【" + dto.getDeviceNo() + "】的设备不存在");
            return CommonResult.success(vo);
        } else {
            vo.setCheckDeviceStatus(1);
            vo.setCheckDeviceMsg("编号【" + dto.getDeviceNo() + "】的设备存在");
            return CommonResult.success(vo);
        }

    }

    /**
     * 扫码充电-详情
     *
     * @return EHomeVo
     */
    @PostMapping("/details")
    public CommonResult<EHomeVo> details(@RequestBody EHomeDto dto) {

        if (dto.getDeviceNo() == null) return CommonResult.validateFailed("参数【deviceNo】不能为空");
        if (dto.getAccessToken() == null) return CommonResult.validateFailed("参数【accessToken】不能为空");

        //根据设备编号判断是否存在改设备
        QueryWrapper<EDevice> dqw = new QueryWrapper<>();
        dqw.eq("device_no", dto.getDeviceNo());
        Integer deviceCount = deviceService.getBaseMapper().selectCount(dqw);
        if (deviceCount == 0) return CommonResult.success(null, "编号【" + dto.getDeviceNo() + "】的设备不存在");

        //通过设备编号获取小区信息并修改用户所在小区的相关信息
        EDevice device = deviceService.getBaseMapper().selectOne(dqw);

        if (device == null) return CommonResult.success(null, "编号【" + dto.getDeviceNo() + "】的设备不存在");

        ECommunity community = communityService.getById(device.getCommunityId());
        if (community == null) return CommonResult.success(null, "编号【" + device.getCommunityId() + "】的小区不存在");

        //通过校验用户接口持久化用户信息
        Pair<Object, String> pair = smartCommunityService.checkUser(dto.getAccessToken());
        if (pair.getFirst() == null) {
            EHomeVo vo = new EHomeVo();
            vo.setCheckUserMsg("2");
            return CommonResult.unauthorized(vo);
        }
        EMember member = (EMember) pair.getFirst();
        if (member == null) {
            //封装前端数据
            EHomeVo vo = packagingHomeVo(dto, community);
            if (vo == null) return CommonResult.success(null, "socket get port status failed!");
            vo.setCheckUserMsg("2");
            return CommonResult.success(vo);
        } else {

            //封装前端数据
            EHomeVo vo = packagingHomeVo(dto, community);

            if (vo == null) return CommonResult.success(null, "socket get port status failed!");
            vo.setCheckUserMsg("1");

            return CommonResult.success(vo);
        }
    }

    private EHomeVo packagingHomeVo(EHomeDto dto, ECommunity community) {

        EHomeVo vo = new EHomeVo();
        vo.setRate(community.getRate());
        vo.setRemark(community.getRemark());

        //插座
        ArrayList<ESocketVo> socketVos = new ArrayList<>();

        //通过调用socket获取插座状态
        List statusByCode = smartCommunityService.getDeviceStatus(dto.getDeviceNo());
        log.info("getDeviceStatus:【" + statusByCode + "】");
//        List<DeviceStatus> statusByCode = DeviceHandler.getDeviceStatusByCode(dto.getDeviceNo());

        if (statusByCode == null || statusByCode.size() == 0) {

            //当获取不到socket的端口时暂时直接根据设备编号获取插座列表 todo 后期需要更改 改为提示：获取设备插座异常，暂时不能提供充电
            QueryWrapper<ESocket> sq = new QueryWrapper<>();
            sq.eq("device_no", dto.getDeviceNo());
            List<ESocket> sockets = socketService.list(sq);
            for (ESocket socket : sockets) {
                ESocketVo socketVo = new ESocketVo();
                socketVo.setPort(socket.getPortNo() + "");
                socketVo.setStatus(socket.getStatus() + "");
                socketVo.setSocketId(socket.getId().toString());
                socketVo.setDeviceSocketStatus(2);
                socketVos.add(socketVo);
            }
        } else {
            for (Object socket : statusByCode) {

                Map m = JSON.parseObject(socket.toString(), Map.class);
                ESocketVo socketVo = new ESocketVo();
                socketVo.setPort(m.get("port") + "");
                socketVo.setStatus(m.get("status") + "");

                //获取插座id
                QueryWrapper<ESocket> socketQueryWrapper = new QueryWrapper<>();
                socketQueryWrapper.eq("device_no", dto.getDeviceNo());
                socketQueryWrapper.eq("port_no", socketVo.getPort());
                ESocket eSocket = socketService.getOne(socketQueryWrapper);
                if (eSocket == null) continue;
                socketVo.setSocketId(eSocket.getId().toString());
                socketVo.setDeviceSocketStatus(1);

                socketVos.add(socketVo);
            }
        }
        vo.setSockets(socketVos);

        return vo;
    }

    @GetMapping("/testing")
    public CommonResult<?> test() {
        Object re = smartCommunityService.checkUser("Q2RkSG9kY09sVGtkeFVLa0hvNHc1VExOQmxuQ0VGa25tOTRCQW9qcXdqZmNvQWtHYklRWUV3PT0");
        return CommonResult.success(re);
    }

    /**
     * 扫码充电-正在充电
     *
     * @return EHomeVo
     */
    @PostMapping("/charging")
    public CommonResult<EHomeVo> chargeNow(@RequestBody EHomeDto dto) {
        log.info("前端传来的参数有:accessToken = " + dto.getAccessToken());
        String spbillCreateIp3 = null;
        try {
            spbillCreateIp3 = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info("spbillCreateIp 3【" + spbillCreateIp3 + "】");

        if (dto.getAccessToken() == null) return CommonResult.success(null, "参数【accessToken】不能为空");

        QueryWrapper<EOrder> oq = new QueryWrapper<>();
        oq.eq("status", 1);//充电中
        oq.ne("pay_status", 5);//剔除完成了的订单

        Pair<Object, String> pair = smartCommunityService.checkUser(dto.getAccessToken());
        if (pair.getFirst() == null) {
            EHomeVo vo = new EHomeVo();
            vo.setCheckUserMsg("2");
            return CommonResult.unauthorized(vo);
        }

        EMember member = (EMember) pair.getFirst();
        oq.eq("member_id", member.getId());
        List<EOrder> orders = orderService.list(oq);

        EHomeVo vo = new EHomeVo();
        vo.setCheckUserMsg("1");

        if (orders.isEmpty()) {
            vo.setChargeStatus(3);
            return CommonResult.success(vo);
        } else {
            EOrder order = orders.get(0);
            //查询充电桩设备插座的空闲状态 - 更新订单状态
            EDevice de = deviceService.getById(order.getDeviceId());
            if (de != null) {
                List deviceStatus = smartCommunityService.getDeviceStatus(de.getDeviceNo());
                if (deviceStatus == null || deviceStatus.size() == 0) { //离线
                    order.setStatus(2);
                    order.setUpdateTime(new Date());
                    order.setChargeTime("0");
                    vo.setChargeStatus(2);
                    log.info("================================离线正在充电订单：" + order);
                    orderService.updateById(order);
//                    return CommonResult.success(vo);
                } else {//在线
                    ESocket socket1 = socketService.getById(order.getSocketId());
                    vo.setEndTime(socket1.getEndTime());
                    vo.setChargeStatus(1);
                    log.info("================================socket:" + socket1);
                    Integer orderSocketStatus = socket1.getStatus();
                    if (orderSocketStatus == 0) order.setStatus(1);//正在充电
                    Integer orderSocketPort = socket1.getPortNo();
                    for (Object portAndStatus : deviceStatus) {
                        Map m = JSON.parseObject(portAndStatus.toString(), Map.class);
                        Object port = m.get("port");
                        Object status = m.get("status");
                        log.info("================================正在充电设备插座端口及状态：" + port + ":" + status);
                        log.info("================================在线正在充电订单：" + order);
                        if (Integer.parseInt(port.toString()) == orderSocketPort) { //更新订单状态
//                            if (status.equals(1)) {//空闲
                            if (Integer.parseInt(status.toString()) == 0) {//使用
                                //判断充电时间是否在当前时间范围内 从而设置是否充电完成
                                log.info("=========1.equals(status) else=========");
                                Date startTime = socket1.getStartTime();
                                Date endTime = socket1.getEndTime();
                                if (startTime == null || endTime == null) {
                                    return CommonResult.success(null, "订单数据异常");
                                } else {
                                    if (TimeUtils.belongCalendar(new Date(), startTime, endTime)) {
                                        log.info("=========TimeUtils.belongCalendar(new Date(), startTime, endTime)=========");
                                        vo.setChargeStatus(1);
                                        order.setStatus(1);
                                        order.setUpdateTime(new Date());
                                        orderService.updateById(order);
//                                        return CommonResult.success(vo);
                                    } else {
                                        log.info("=========TimeUtils.belongCalendar(new Date(), startTime, endTime)  else=========");
                                        vo.setChargeStatus(2);
                                        order.setStatus(2);//充电完成
                                        order.setPayStatus(5);//订单完成Basic cGlnOnBpZw==
                                        orderService.updateById(order);
//                                        return CommonResult.success(vo);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

            } else {
                return CommonResult.success(null, "ID为" + order.getDeviceId() + "的设备不存在");
            }
        }
        return CommonResult.success(vo);
    }

    /**
     * 去支付
     *
     * @return EHomeVo
     */
    @PostMapping("go/pay")
    public CommonResult<Map<String, String>> goPay(@RequestBody EHomeDto dto) {

        HashMap<String, String> map = new HashMap<>();

        PayWay payway;

        if (dto.getPayWay() == null || (payway = PayWay.makePayWay(dto.getPayWay())) == null) {
            map.put("status", "0");
            map.put("data", "支付方式payWay参数错误");
            return CommonResult.success(map);
        }

        if (dto.getAccessToken() == null || dto.getHour() == null || dto.getSocketId() == null) {
            map.put("status", "0");
            map.put("data", "accessToken或Hour或SocketId参数不能为空");
            return CommonResult.success(map);
        }


        //通过校验用户接口持久化用户信息
        Pair<Object, String> pair = smartCommunityService.checkUser(dto.getAccessToken());
        if (pair.getFirst() == null) {
            map.put("status", "0");
            map.put("data", "用户信息校验失败");
            return CommonResult.unauthorized(map);
        }
        EMember member = (EMember) pair.getFirst();
        if (member == null) {
            map.put("status", "0");
            map.put("data", "用户不存在");
            return CommonResult.success(map);
        }

        QueryWrapper<EOrder> oq = new QueryWrapper<>();
        oq.eq("status", 1);//充电中
        oq.eq("member_id", member.getId());
        List<EOrder> orders = orderService.list(oq);
        logger.info("ID为【" + member.getId() + "】的用户对应的订单是【" + orders + "】");

        if (!orders.isEmpty()) {
            map.put("status", "0");
            map.put("data", "您有正在充电的订单未完成,请结束后重新发起充电");
            return CommonResult.success(map);
        }


        //生成一个订单
        ESocket socket = socketService.getById(dto.getSocketId());
        logger.info("ID为【" + dto.getSocketId() + "】的插座是【" + socket + "】");

        if (socket == null) {
            map.put("status", "0");
            map.put("data", "ID为【" + dto.getSocketId() + "】的插座不存在");
            return CommonResult.success(map);
        }

        QueryWrapper<EDevice> dqw = new QueryWrapper<>();
        dqw.eq("device_no", socket.getDeviceNo());
        List<EDevice> devices = deviceService.list(dqw);
        logger.info("编号为【" + socket.getDeviceNo() + "】的设备是【" + devices + "】");

        if (devices.isEmpty()) {
            map.put("status", "0");
            map.put("data", "编号:" + dto.getDeviceNo() + "的设备不存在");
            return CommonResult.success(map);
        } else {
            EDevice device = devices.get(0);
            if (device == null) {
                map.put("status", "0");
                map.put("data", "设备不存在");
                return CommonResult.success(map);
            }

            ECommunity community = communityService.getById(device.getCommunityId());
            logger.info("ID为【" + device.getCommunityId() + "】的社区是【" + community + "】");
            if (community == null) {
                map.put("status", "0");
                map.put("data", "ID为【" + device.getCommunityId() + "】的社区不存在");
                return CommonResult.success(map);
            }

            //save order
            EOrder order = new EOrder();
            order.setCommunityId(device.getCommunityId());
            order.setMemberId(member.getId());
            order.setDeviceId(device.getId());
            order.setStatus(0);//待付款后充电
            order.setPayStatus(1);//待付款
            order.setPayWay(dto.getPayWay());


            BigDecimal hour = new BigDecimal(dto.getHour());
            BigDecimal rate = new BigDecimal(community.getRate());
            BigDecimal payMoney = hour.multiply(rate);//元
            order.setPay(payMoney);

            int money = 0; //发起支付的金额

            if (payway == PayWay.WX_PAY) { //单位：分
                BigDecimal multiply = payMoney.multiply(BigDecimal.valueOf(100));
                Double tempD = Double.parseDouble(multiply.toString());
                money = tempD.intValue();
            }

            order.setSocketId(dto.getSocketId());

            String unifiedOrder = DateUtils.getExtractNo();
            order.setOrderNo(unifiedOrder);
            order.setChargeTime(dto.getHour());

            orderService.save(order);

            String payData = null;

            if (payway == PayWay.WX_PAY) {
                String body = "智慧社区充电桩微信支付";//商品描述
                try {

                    String spbillCreateIp3 = InetAddress.getLocalHost().getHostAddress();
                    logger.info("spbillCreateIp 3【" + spbillCreateIp3 + "】");

                    payData = wechatPayService.h5pay(order, body, money, spbillCreateIp3);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }

            if (payway == PayWay.ALI_PAY)
                payData = createAliPayData(unifiedOrder, payMoney);

            if (payData == null) {
                map.put("status", "0");
                map.put("data", "为获取到支付结果数据");
                return CommonResult.success(map);
            }

            log.info("PayData【" + payData + "】");

            if (payway == PayWay.WX_PAY) {
                ZWxPreRecord record = new ZWxPreRecord();
                record.setOutTradeNo(unifiedOrder);
                record.setTotalFee(money);
                if (!wxPreRecordService.save(record)) {
                    map.put("status", "0");
                    map.put("data", "服务器忙");
                    return CommonResult.success(map);
                }
                wxPreRecordService.save(record);
                map.put("status", "2");
                map.put("data", payData);
                return CommonResult.success(map);
            }

            ZAliPreRecord record = new ZAliPreRecord();
            record.setDel(0);
            record.setCreateTime(new Date());
            record.setOutTradeNo(unifiedOrder);
            record.setTotalAmount(CentToDisplay.convert(money));

            if (!aliPreRecordService.save(record)) {
                map.put("data", "服务器忙");
                map.put("status", "0");
                return CommonResult.success(map);
            }
            aliPreRecordService.save(record);
            map.put("status", "3");//阿里支付数据
            map.put("data", payData);
            return CommonResult.success(map);
        }

    }

    /**
     * 停止充电
     *
     * @return Boolean
     */
    @PostMapping("/stop")
    public CommonResult<Boolean> stop(@RequestBody EHomeDto dto) {

        if (dto.getAccessToken() == null) return CommonResult.success(null, "参数【accessToken】不能为空");

        //通过校验用户接口持久化用户信息
        Pair<Object, String> pair = smartCommunityService.checkUser(dto.getAccessToken());
        if (pair.getFirst() == null) {
            return CommonResult.unauthorized(false);
        }

        EMember member = (EMember) pair.getFirst();
        //获取当前用户正在充电的订单
        QueryWrapper<EOrder> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("member_id", member.getId());
        orderQueryWrapper.eq("status", 1);
        List<EOrder> orders = orderService.list(orderQueryWrapper);

        if (orders.size() > 1) {
            logger.info("ID为【" + member.getId() + "】的用户正在充电的数据大于1条");
            return CommonResult.success(false, "ID为【" + member.getId() + "】的用户正在充电的数据异常");
        } else if (orders.size() == 1) {

            EOrder order = orders.get(0);
            order.setUpdateTime(new Date());

            ECommunity community = communityService.getById(order.getCommunityId());
            //todo delete community and delete order of community id
            if (community == null)
                return CommonResult.success(false, "ID为【" + order.getCommunityId() + "】的社区数据已被删除");

            ESocket socket = socketService.getById(order.getSocketId());
            if (socket == null)
                return CommonResult.success(false, "ID为【" + order.getSocketId() + "】的插座数据已被删除");

            //处理退款字段设置
            String chargeTime = TimeUtils.timeDiffer(socket.getEndTime(), new Date());
            log.info("==================================================剩余充电时间为:" + chargeTime);
            //不足一小时安一小时算
            double floor = Math.floor(Double.parseDouble(chargeTime));
            log.info("==================================================不足一小时按一小时算的剩余充电时间为:" + floor);
            if (floor <= 0) order.setRefund(BigDecimal.ZERO);
            BigDecimal multiply = new BigDecimal(floor).multiply(new BigDecimal(community.getRate()));
            if (multiply.compareTo(BigDecimal.ZERO) <= 0) {
                order.setRefund(BigDecimal.ZERO);
            } else {
                order.setRefund(multiply);
            }
            log.info("==================================================订单退款金额为:" + order.getRefund());
            boolean isUpdate;

            // 调用停止充电接口
            try {
                String stopData = smartCommunityService.stopCharge(socket.getDeviceNo(), socket.getPortNo());
                if (stopData != null) {
                    Map stopMap = JSONObject.parseObject(stopData, Map.class);
                    Object result = stopMap.get("data");
                    if (EStringUtils.isEmpty(result.toString())) {
                        return CommonResult.success(false, "远程SOCKET调用停止充电接口数据异常");
                    } else {
                        if ("发送成功".equals(result)) {
                            order.setStatus(2);
                            order.setPayStatus(5);//订单完成
                            socket.setStatus(1);
                            if (order.getRefund().compareTo(BigDecimal.ZERO) > 0) {//退款金额大于0走退款流程
                                //获取支付方式
                                Integer payWay = order.getPayWay();
                                if (payWay == 1) { //支付宝支付
                                    logger.info("付款方式是支付宝");
                                    BigDecimal refund = order.getRefund();
                                    refund = refund.setScale(2, BigDecimal.ROUND_HALF_UP);
                                    isUpdate = aliPayService.refund(order.getOrderNo(), refund.toString());
                                } else if (payWay == 2) {//微信支付
                                    logger.info("付款方式是微信");
                                    isUpdate = wechatPayService.refund(order.getOrderNo(), order.getRefund().toString());
                                } else {
                                    logger.info("付款方式不是支付宝也不是微信,请检查数据库数据");
                                    return CommonResult.success(false, "数据异常");
                                }
                            } else {
                                isUpdate = true;
                            }
                            log.info("是否更新数据【" + isUpdate + "】");
                            //更新数据
                            if (isUpdate) {
                                if (socketService.updateById(socket))
                                    return CommonResult.success(orderService.updateById(order));
                            } else {
                                return CommonResult.success(false, "数据更新失败");
                            }
                        } else {
                            return CommonResult.success(false, "远程SOCKET调用停止充电接口发生失败");
                        }
                    }
                } else {
                    return CommonResult.success(false, "远程SOCKET调用停止充电接口数据异常");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return CommonResult.success(false, "设备【" + socket.getDeviceNo() + "】【" + socket.getPortNo() + "】号端口异常");
            }
        } else {
            return CommonResult.success(false, "ID为【" + member.getId() + "】的用户没有正在充电的订单");
        }
        return CommonResult.success(false, "系统异常");
    }

    public static void main(String[] args) {
        String chargeTime = "3";
        double floor = Math.floor(Double.parseDouble(chargeTime));
        BigDecimal multiply = new BigDecimal(floor).multiply(new BigDecimal("0.01"));
        BigDecimal subtract = new BigDecimal("0.04").subtract(multiply);
        if (multiply.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("subtract = " + subtract);
        } else {
            System.out.println("subtract = " + subtract);
            System.out.println("multiply = " + multiply);
            BigDecimal subtract1 = new BigDecimal("0.04").subtract(multiply);
            System.out.println("subtract1 = " + subtract1);
        }

    }


    /**
     * 订单记录
     *
     * @return Boolean
     */
    @PostMapping("/history")
    public CommonResult<Map<String, Object>> history(@RequestParam Map<String, Object> params) {

        String accessToken = params.get("accessToken").toString();

        Map<String, Object> map = new HashMap<>();

        if (accessToken == null) return CommonResult.success(null, "参数【accessToken】不能为空");

        //通过校验用户接口持久化用户信息
        Pair<Object, String> pair = smartCommunityService.checkUser(accessToken);
        if (pair.getFirst() == null) {
            map.put("status", "2");
            return CommonResult.unauthorized(map);
        } else {
            map.put("status", "1");
        }

        EMember member = (EMember) pair.getFirst();

        Assert.isNull(accessToken, "参数【accessToken】不能为空");
        QueryWrapper<EOrder> oq = new QueryWrapper<>();
        oq.eq("member_id", member.getId());
        oq.orderByDesc("create_time");
        IPage<EOrder> orders = orderService.page(new Query<>(params), oq);
        ArrayList<EOrderVo> datas = new ArrayList<>();
        for (EOrder order : orders.getRecords()) {
            EOrderVo vo = new EOrderVo();
            vo.setCreateTime(order.getCreateTime());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());
            if (order.getRefund() == null) {
                vo.setRefund("0.00");
                vo.setRefundStatus(2);
            } else {
                vo.setRefund(order.getRefund() + "");
                vo.setRefundStatus(1);
            }

            ESocket socket = socketService.getById(order.getSocketId());
            if (socket == null) continue;

//            String chargeTime = "0.0";
//            if (socket.getStartTime() != null) {
//                chargeTime = TimeUtils.timeDiffer(new Date(), socket.getStartTime());
//            }

            vo.setChargeTime(order.getChargeTime());
//            vo.setChargeTime(chargeTime);

            if (order.getPayStatus() == 2 || order.getPayStatus() == 5)
                vo.setPay(order.getPay().toString());
            else
                vo.setPay("0.00");

            EDevice device = deviceService.getById(order.getDeviceId());
            if (device == null) continue;

//            ECommunity community = communityService.getById(device.getCommunityId());
//            BigDecimal multiply = new BigDecimal(chargeTime).multiply(new BigDecimal(community.getRate()));
//            BigDecimal subtract = order.getPay().subtract(multiply);
//            vo.setActualPay(subtract.toString());
            datas.add(vo);
        }
        map.put("total", orders.getTotal());
        map.put("current", orders.getCurrent());
        map.put("size", orders.getSize());
        map.put("page", orders.getPages());
        map.put("data", datas);

        return CommonResult.success(map);
    }


    /**
     * 社区列表
     *
     * @return CommonResult
     */
    @PostMapping("/communitys")
    public CommonResult<List<ECommunity>> communitys() {
        List<ECommunity> communities = communityService.list();
        return CommonResult.success(communities);
    }


    @PostMapping("/test1")
    public void test1(@RequestBody EHomeDto dto) {
        System.out.println("aliPayService.refund(dto.getOrderNo()) = " + aliPayService.refund(dto.getOrderNo(), "0.01"));
    }

    @PostMapping("/test2")
    public void test2(@RequestBody EHomeDto dto) {
        System.out.println("aliPayService.refundQuery(dto.getOrderNo()) = " + aliPayService.refundQuery(dto.getOrderNo()));
    }

    @PostMapping("/test3")
    public void test3(@RequestBody EHomeDto dto) {
        System.out.println("aliPayService.refundQuery(dto.getOrderNo()) = " + smartCommunityService.checkUser(dto.getOrderNo()));
    }

    @PostMapping("/test4")
    public void test4(@RequestBody EHomeDto dto) {

    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody EHomeDto dto) {
        System.out.println("aliPayService.refundQuery(dto.getOrderNo()) = " + smartCommunityService.checkUser4UserLogin(dto.getOrderNo()));
    }

    @GetMapping("/notices")
    public CommonResult<ArrayList<Notices>> notices() {

        ArrayList<Notices> data = new ArrayList<>();

        pageData4notices(data);

        return CommonResult.success(data);
    }

    private void pageData4notices(ArrayList<Notices> data) {
        Notices notices1 = new Notices();
        notices1.setId("000000001");
        notices1.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png");
        notices1.setTitle("你收到了 14 份新周报");
        notices1.setDatetime("2017-08-09");
        notices1.setType("notification");
        data.add(notices1);

        Notices notices2 = new Notices();
        notices2.setId("000000002");
        notices2.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/OKJXDXrmkNshAMvwtvhu.png");
        notices2.setTitle("你推荐的 曲妮妮 已通过第三轮面试");
        notices2.setDatetime("2017-08-08");
        notices2.setType("notification");
        data.add(notices2);

        Notices notices3 = new Notices();
        notices3.setId("000000003");
        notices3.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/kISTdvpyTAhtGxpovNWd.png");
        notices3.setTitle("这种模板可以区分多种通知类型");
        notices3.setDatetime("2017-08-07");
        notices3.setType("notification");
        notices3.setRead(true);
        data.add(notices3);

        Notices notices4 = new Notices();
        notices4.setId("000000004");
        notices4.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/GvqBnKhFgObvnSGkDsje.png");
        notices4.setTitle("左侧图标用于区分不同的类型");
        notices4.setDatetime("2017-08-07");
        notices4.setType("notification");
        data.add(notices4);

        Notices notices5 = new Notices();
        notices5.setId("000000005");
        notices5.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png");
        notices5.setTitle("内容不要超过两行字，超出时自动截断");
        notices5.setDatetime("2017-08-07");
        notices5.setType("notification");
        data.add(notices5);

        Notices notices6 = new Notices();
        notices6.setId("000000006");
        notices6.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/fcHMVNCjPOsbUGdEduuv.jpeg");
        notices6.setTitle("曲丽丽 评论了你");
        notices6.setDatetime("2017-08-07");
        notices6.setType("message");
        notices6.setDescription("描述信息描述信息描述信息");
        notices6.setClickClose(true);
        data.add(notices6);

        Notices notices7 = new Notices();
        notices7.setId("000000007");
        notices7.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/fcHMVNCjPOsbUGdEduuv.jpeg");
        notices7.setTitle("朱偏右 回复了你");
        notices7.setDescription("这种模板用于提醒谁与你发生了互动，左侧放『谁』的头像");
        notices7.setDatetime("2017-08-07");
        notices7.setType("message");
        notices7.setClickClose(true);
        data.add(notices7);

        Notices notices8 = new Notices();
        notices8.setId("000000008");
        notices8.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/fcHMVNCjPOsbUGdEduuv.jpeg");
        notices8.setTitle("标题");
        notices8.setDescription("这种模板用于提醒谁与你发生了互动，左侧放『谁』的头像");
        notices8.setDatetime("2017-08-07");
        notices8.setType("message");
        notices8.setClickClose(true);
        data.add(notices8);

        Notices notices9 = new Notices();
        notices9.setId("000000009");
        notices9.setTitle("任务名称");
        notices9.setDescription("任务需要在 2017-01-12 20:00 前启动");
        notices9.setExtra("未开始");
        notices9.setStatus("todo");
        notices9.setType("event");
        data.add(notices9);

        Notices notices10 = new Notices();
        notices10.setId("000000010");
        notices10.setTitle("第三方紧急代码变更");
        notices10.setDescription("冠霖提交于 2017-01-06，需在 2017-01-07 前完成代码变更任务");
        notices10.setExtra("马上到期");
        notices10.setStatus("urgent");
        notices10.setType("event");
        data.add(notices10);

        Notices notices11 = new Notices();
        notices11.setId("000000011");
        notices11.setTitle("信息安全考试");
        notices11.setDescription("指派竹尔于 2017-01-09 前完成更新并发布");
        notices11.setExtra("已耗时 8 天");
        notices11.setStatus("doing");
        notices11.setType("event");
        data.add(notices11);

        Notices notices12 = new Notices();
        notices12.setId("000000012");
        notices12.setTitle("ABCD 版本发布");
        notices12.setDescription("冠霖提交于 2017-01-06，需在 2017-01-07 前完成代码变更任务");
        notices12.setExtra("进行中");
        notices12.setStatus("processing");
        notices12.setType("event");
        data.add(notices12);
    }

    @GetMapping("/currentUser")
    public CommonResult<ArrayList<CurrentUser>> currentUser() {
        ArrayList<CurrentUser> data = new ArrayList<>();

        CurrentUser user = new CurrentUser();
        user.setName("Serati Ma");
        user.setAvatar("https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png");
        user.setUserid("00000001");
        user.setEmail("antdesign@alipay.com");
        user.setSignature("海纳百川，有容乃大");
        user.setTitle("交互专家");
        user.setGroup("蚂蚁金服－某某某事业群－某某平台部－某某技术部－UED");
        ArrayList<Tags> tags = new ArrayList<>();
        Tags tag0 = new Tags();
        tag0.setKey("0");
        tag0.setKey("很有想法的");
        tags.add(tag0);

        Tags tag1 = new Tags();
        tag1.setKey("1");
        tag1.setKey("专注设计");
        tags.add(tag1);

        Tags tag2 = new Tags();
        tag2.setKey("2");
        tag2.setKey("辣~");
        tags.add(tag2);

        Tags tag3 = new Tags();
        tag3.setKey("3");
        tag3.setKey("大长腿");
        tags.add(tag3);

        Tags tag4 = new Tags();
        tag4.setKey("4");
        tag4.setKey("川妹子");
        tags.add(tag4);

        Tags tag5 = new Tags();
        tag5.setKey("4");
        tag5.setKey("海纳百川");
        tags.add(tag5);

        user.setTags(tags);

        user.setNotifyCount(12);
        user.setUnreadCount(11);
        user.setCountry("China");

        List<List<Tags>> geographics = new ArrayList<>();
        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        Geographic geographic = new Geographic();
        geographic.setType("province");
        Tags tags1 = new Tags();
        tags1.setKey("330000");
        tags1.setLabel("浙江省");
        tagsArrayList.add(tags1);
        Tags tags2 = new Tags();
        tags1.setKey("330100");
        tags1.setLabel("杭州市");
        tagsArrayList.add(tags2);
        geographic.setTags(tagsArrayList);

        geographics.add(tagsArrayList);
        user.setGeographic(geographics);
        data.add(user);

        user.setAddress("西湖区工专路 77 号");
        user.setPhone("0752-268888888");


        return CommonResult.success(data);
    }

    @GetMapping("/fake_chart_data")
    public CommonResult<Object> fakeChartData() {
        String json4string = "{\"visitData\":[{\"x\":\"2020-02-22\",\"y\":7},{\"x\":\"2020-02-23\",\"y\":5},{\"x\":\"2020-02-24\",\"y\":4},{\"x\":\"2020-02-25\",\"y\":2},{\"x\":\"2020-02-26\",\"y\":4},{\"x\":\"2020-02-27\",\"y\":7},{\"x\":\"2020-02-28\",\"y\":5},{\"x\":\"2020-02-29\",\"y\":6},{\"x\":\"2020-03-01\",\"y\":5},{\"x\":\"2020-03-02\",\"y\":9},{\"x\":\"2020-03-03\",\"y\":6},{\"x\":\"2020-03-04\",\"y\":3},{\"x\":\"2020-03-05\",\"y\":1},{\"x\":\"2020-03-06\",\"y\":5},{\"x\":\"2020-03-07\",\"y\":3},{\"x\":\"2020-03-08\",\"y\":6},{\"x\":\"2020-03-09\",\"y\":5}],\"visitData2\":[{\"x\":\"2020-02-22\",\"y\":1},{\"x\":\"2020-02-23\",\"y\":6},{\"x\":\"2020-02-24\",\"y\":4},{\"x\":\"2020-02-25\",\"y\":8},{\"x\":\"2020-02-26\",\"y\":3},{\"x\":\"2020-02-27\",\"y\":7},{\"x\":\"2020-02-28\",\"y\":2}],\"salesData\":[{\"x\":\"1月\",\"y\":872},{\"x\":\"2月\",\"y\":252},{\"x\":\"3月\",\"y\":333},{\"x\":\"4月\",\"y\":778},{\"x\":\"5月\",\"y\":706},{\"x\":\"6月\",\"y\":456},{\"x\":\"7月\",\"y\":915},{\"x\":\"8月\",\"y\":1016},{\"x\":\"9月\",\"y\":1191},{\"x\":\"10月\",\"y\":1028},{\"x\":\"11月\",\"y\":305},{\"x\":\"12月\",\"y\":781}],\"searchData\":[{\"index\":1,\"keyword\":\"搜索关键词-0\",\"count\":209,\"range\":66,\"status\":0},{\"index\":2,\"keyword\":\"搜索关键词-1\",\"count\":721,\"range\":13,\"status\":1},{\"index\":3,\"keyword\":\"搜索关键词-2\",\"count\":274,\"range\":7,\"status\":1},{\"index\":4,\"keyword\":\"搜索关键词-3\",\"count\":581,\"range\":82,\"status\":0},{\"index\":5,\"keyword\":\"搜索关键词-4\",\"count\":221,\"range\":87,\"status\":0},{\"index\":6,\"keyword\":\"搜索关键词-5\",\"count\":330,\"range\":66,\"status\":0},{\"index\":7,\"keyword\":\"搜索关键词-6\",\"count\":734,\"range\":55,\"status\":0},{\"index\":8,\"keyword\":\"搜索关键词-7\",\"count\":196,\"range\":93,\"status\":0},{\"index\":9,\"keyword\":\"搜索关键词-8\",\"count\":229,\"range\":24,\"status\":0},{\"index\":10,\"keyword\":\"搜索关键词-9\",\"count\":668,\"range\":21,\"status\":0},{\"index\":11,\"keyword\":\"搜索关键词-10\",\"count\":464,\"range\":94,\"status\":0},{\"index\":12,\"keyword\":\"搜索关键词-11\",\"count\":540,\"range\":39,\"status\":1},{\"index\":13,\"keyword\":\"搜索关键词-12\",\"count\":252,\"range\":6,\"status\":0},{\"index\":14,\"keyword\":\"搜索关键词-13\",\"count\":9,\"range\":85,\"status\":0},{\"index\":15,\"keyword\":\"搜索关键词-14\",\"count\":706,\"range\":3,\"status\":1},{\"index\":16,\"keyword\":\"搜索关键词-15\",\"count\":863,\"range\":22,\"status\":1},{\"index\":17,\"keyword\":\"搜索关键词-16\",\"count\":798,\"range\":77,\"status\":0},{\"index\":18,\"keyword\":\"搜索关键词-17\",\"count\":484,\"range\":47,\"status\":0},{\"index\":19,\"keyword\":\"搜索关键词-18\",\"count\":384,\"range\":9,\"status\":1},{\"index\":20,\"keyword\":\"搜索关键词-19\",\"count\":542,\"range\":68,\"status\":0},{\"index\":21,\"keyword\":\"搜索关键词-20\",\"count\":889,\"range\":27,\"status\":1},{\"index\":22,\"keyword\":\"搜索关键词-21\",\"count\":960,\"range\":51,\"status\":0},{\"index\":23,\"keyword\":\"搜索关键词-22\",\"count\":186,\"range\":4,\"status\":0},{\"index\":24,\"keyword\":\"搜索关键词-23\",\"count\":597,\"range\":75,\"status\":0},{\"index\":25,\"keyword\":\"搜索关键词-24\",\"count\":160,\"range\":1,\"status\":0},{\"index\":26,\"keyword\":\"搜索关键词-25\",\"count\":671,\"range\":6,\"status\":1},{\"index\":27,\"keyword\":\"搜索关键词-26\",\"count\":329,\"range\":30,\"status\":1},{\"index\":28,\"keyword\":\"搜索关键词-27\",\"count\":571,\"range\":79,\"status\":0},{\"index\":29,\"keyword\":\"搜索关键词-28\",\"count\":821,\"range\":91,\"status\":0},{\"index\":30,\"keyword\":\"搜索关键词-29\",\"count\":161,\"range\":22,\"status\":0},{\"index\":31,\"keyword\":\"搜索关键词-30\",\"count\":339,\"range\":56,\"status\":1},{\"index\":32,\"keyword\":\"搜索关键词-31\",\"count\":670,\"range\":19,\"status\":1},{\"index\":33,\"keyword\":\"搜索关键词-32\",\"count\":380,\"range\":56,\"status\":0},{\"index\":34,\"keyword\":\"搜索关键词-33\",\"count\":797,\"range\":68,\"status\":1},{\"index\":35,\"keyword\":\"搜索关键词-34\",\"count\":865,\"range\":57,\"status\":0},{\"index\":36,\"keyword\":\"搜索关键词-35\",\"count\":314,\"range\":42,\"status\":1},{\"index\":37,\"keyword\":\"搜索关键词-36\",\"count\":179,\"range\":61,\"status\":0},{\"index\":38,\"keyword\":\"搜索关键词-37\",\"count\":588,\"range\":73,\"status\":1},{\"index\":39,\"keyword\":\"搜索关键词-38\",\"count\":140,\"range\":72,\"status\":1},{\"index\":40,\"keyword\":\"搜索关键词-39\",\"count\":382,\"range\":64,\"status\":1},{\"index\":41,\"keyword\":\"搜索关键词-40\",\"count\":37,\"range\":33,\"status\":1},{\"index\":42,\"keyword\":\"搜索关键词-41\",\"count\":32,\"range\":72,\"status\":1},{\"index\":43,\"keyword\":\"搜索关键词-42\",\"count\":95,\"range\":54,\"status\":1},{\"index\":44,\"keyword\":\"搜索关键词-43\",\"count\":780,\"range\":26,\"status\":1},{\"index\":45,\"keyword\":\"搜索关键词-44\",\"count\":858,\"range\":33,\"status\":1},{\"index\":46,\"keyword\":\"搜索关键词-45\",\"count\":885,\"range\":87,\"status\":0},{\"index\":47,\"keyword\":\"搜索关键词-46\",\"count\":548,\"range\":92,\"status\":0},{\"index\":48,\"keyword\":\"搜索关键词-47\",\"count\":122,\"range\":3,\"status\":1},{\"index\":49,\"keyword\":\"搜索关键词-48\",\"count\":308,\"range\":32,\"status\":1},{\"index\":50,\"keyword\":\"搜索关键词-49\",\"count\":216,\"range\":44,\"status\":1}],\"offlineData\":[{\"name\":\"Stores 0\",\"cvr\":0.2},{\"name\":\"Stores 1\",\"cvr\":0.8},{\"name\":\"Stores 2\",\"cvr\":0.3},{\"name\":\"Stores 3\",\"cvr\":0.3},{\"name\":\"Stores 4\",\"cvr\":0.8},{\"name\":\"Stores 5\",\"cvr\":0.3},{\"name\":\"Stores 6\",\"cvr\":0.2},{\"name\":\"Stores 7\",\"cvr\":0.5},{\"name\":\"Stores 8\",\"cvr\":0.3},{\"name\":\"Stores 9\",\"cvr\":0.3}],\"offlineChartData\":[{\"x\":1582345821464,\"y1\":102,\"y2\":96},{\"x\":1582347621464,\"y1\":53,\"y2\":76},{\"x\":1582349421464,\"y1\":63,\"y2\":101},{\"x\":1582351221464,\"y1\":20,\"y2\":74},{\"x\":1582353021464,\"y1\":68,\"y2\":38},{\"x\":1582354821464,\"y1\":14,\"y2\":95},{\"x\":1582356621464,\"y1\":108,\"y2\":83},{\"x\":1582358421464,\"y1\":74,\"y2\":106},{\"x\":1582360221464,\"y1\":33,\"y2\":42},{\"x\":1582362021464,\"y1\":75,\"y2\":78},{\"x\":1582363821464,\"y1\":13,\"y2\":84},{\"x\":1582365621464,\"y1\":44,\"y2\":86},{\"x\":1582367421464,\"y1\":78,\"y2\":48},{\"x\":1582369221464,\"y1\":37,\"y2\":44},{\"x\":1582371021464,\"y1\":40,\"y2\":71},{\"x\":1582372821464,\"y1\":106,\"y2\":59},{\"x\":1582374621464,\"y1\":108,\"y2\":51},{\"x\":1582376421464,\"y1\":100,\"y2\":106},{\"x\":1582378221464,\"y1\":68,\"y2\":27},{\"x\":1582380021464,\"y1\":68,\"y2\":107}],\"salesTypeData\":[{\"x\":\"男性业主\",\"y\":4544},{\"x\":\"女性业主\",\"y\":3321},{\"x\":\"儿童\",\"y\":3113},{\"x\":\"外来人数\",\"y\":2341},{\"x\":\"物管人员\",\"y\":1231}],\"salesTypeDataOnline\":[{\"x\":\"男性业主\",\"y\":4544},{\"x\":\"女性业主\",\"y\":3321},{\"x\":\"儿童\",\"y\":3113},{\"x\":\"外来人数\",\"y\":2341},{\"x\":\"物管人员\",\"y\":1231}],\"salesTypeDataOffline\":[{\"x\":\"男性业主\",\"y\":4544},{\"x\":\"女性业主\",\"y\":3321},{\"x\":\"儿童\",\"y\":3113},{\"x\":\"外来人数\",\"y\":2341},{\"x\":\"物管人员\",\"y\":1231}],\"radarData\":[{\"name\":\"个人\",\"label\":\"引用\",\"value\":10},{\"name\":\"个人\",\"label\":\"口碑\",\"value\":8},{\"name\":\"个人\",\"label\":\"产量\",\"value\":4},{\"name\":\"个人\",\"label\":\"贡献\",\"value\":5},{\"name\":\"个人\",\"label\":\"热度\",\"value\":7},{\"name\":\"团队\",\"label\":\"引用\",\"value\":3},{\"name\":\"团队\",\"label\":\"口碑\",\"value\":9},{\"name\":\"团队\",\"label\":\"产量\",\"value\":6},{\"name\":\"团队\",\"label\":\"贡献\",\"value\":3},{\"name\":\"团队\",\"label\":\"热度\",\"value\":1},{\"name\":\"部门\",\"label\":\"引用\",\"value\":4},{\"name\":\"部门\",\"label\":\"口碑\",\"value\":1},{\"name\":\"部门\",\"label\":\"产量\",\"value\":6},{\"name\":\"部门\",\"label\":\"贡献\",\"value\":5},{\"name\":\"部门\",\"label\":\"热度\",\"value\":7}]}";
        Object parse = JSONObject.parse(json4string);
        return CommonResult.success(parse);
    }


}
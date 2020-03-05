package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class EHomeVo {

    //收费
    private String rate;

    //查看详情
    private String remark;

    //插座
    private List<ESocketVo> sockets;

    //充电状态 1：正在充电 2：充电完成 3:支付失败
    private Integer chargeStatus;

    //充电结束时间
    private Date endTime;

    //校验充电桩是否存在 1：存在  2：不在
    private Integer checkDeviceStatus;

    //校验充电桩是否存在 1：存在  2：不在
    private String checkDeviceMsg;

    //校验用户信息 1：成功  2：失败
    private String checkUserMsg;
}

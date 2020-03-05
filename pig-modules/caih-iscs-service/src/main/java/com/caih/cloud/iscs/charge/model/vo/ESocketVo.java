package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

@Data
public class ESocketVo {

    //插座端口号
    private String port;

    //插座状态 0：使用中 1：空闲中
    private String status;

    //插座ID
    private String socketId;

    //设备插座数据是否获取到 1：获取到 2：未获取到,将使用默认的但是不能进行充电，可以支付
    private Integer deviceSocketStatus;

}
package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 充电桩-插座表
 */
@Data
@TableName("e_socket")
public class ESocket extends BaseEntity {


    /**
     * 充电桩设备编号
     */
    private String deviceNo;

    /**
     * 插座端口编号
     */
    private Integer portNo;

    /**
     * 插座状态：0:使用中 1:空闲中
     */
    private Integer status;

    /**
     * 充电开始时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 充电结束时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}

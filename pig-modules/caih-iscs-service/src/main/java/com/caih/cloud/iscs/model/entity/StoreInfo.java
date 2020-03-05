package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("iscs_store_info")
public class StoreInfo extends BaseEntity {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商户编号
     */
    private String storeNo;

    /**
     * 商户名称
     */
    private String storeName;

    /**
     * 登录账号
     */
    private String accNo;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 账户余额
     */
    private BigDecimal accBalance;

    /**
     * 通联收银宝商户号(用于交易收款,退款)
     */
    private String cusid;
    /**
     * 通联收银宝APPID
     */
    private String appid;
    /**
     * 通联收银宝MD5key
     */
    private String md5key;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}

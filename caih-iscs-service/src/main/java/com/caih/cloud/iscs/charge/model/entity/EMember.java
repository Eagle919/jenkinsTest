package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

/**
 * 充电桩-用户表
 */
@Data
@TableName("e_member")
public class EMember extends BaseEntity {


    /**
     * 用户编号
     */
    private String memberNo;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户头像 n
     */
    private String avatar;

    /**
     * 用户手机号码 n
     */
    private String mobileTel;

    /**
     * 用户电话号码 n
     */
    private String tel;

    /**
     * 用户y邮箱 n
     */
    private String email;

}

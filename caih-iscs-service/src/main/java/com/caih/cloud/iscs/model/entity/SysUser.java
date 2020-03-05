package com.caih.cloud.iscs.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer userId;

    private String username;

    private String password;

    private String salt;

    private String phone;

    private String avatar;

    private Integer deptId;

    private Date createTime;

    private Date updateTime;

    private String delFlag;
}

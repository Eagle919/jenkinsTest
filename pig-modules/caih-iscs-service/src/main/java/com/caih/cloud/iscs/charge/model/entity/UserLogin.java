package com.caih.cloud.iscs.charge.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.caih.cloud.iscs.model.base.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 充电桩-基本用户数据
 */
@Data
@TableName("user_login")
public class UserLogin extends BaseEntity {
    private String fullName;
    private String avatar;
    private String mobileTel;
    private String email;
    private String tel;
    private String token;
    private String communityList;
    private String buildingList;
}

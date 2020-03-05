package com.caih.cloud.iscs.model.qo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
public class StoreQo {
    @NotBlank(message = "商户编号不能为空")
    @Length(max = 36,message = "商户编号过长")
    private String storeNo;
    @NotBlank(message = "商户名称不能为空")
    @Length(max = 50,message = "商户名称过长")
    private String storeName;
    @NotBlank(message = "登录账号不能为空")
    @Length(max = 36,message = "登录账号过长")
    private String accNo;
    @NotBlank(message = "联系人名称不能为空")
    @Length(max = 50,message = "联系人名称过长")
    private String userName;

    private String phone;

    @NotBlank(message = "通联收银宝商户号不能为空")
    @Length(max = 50,message = "通联收银宝商户号过长")
    private String cusid;
    @NotBlank(message = "通联收银宝APPID不能为空")
    @Length(max = 50,message = "通联收银宝APPID过长")
    private String appid;
    @NotBlank(message = "通联收银宝MD5key不能为空")
    @Length(max = 50,message = "通联收银宝MD5key过长")
    private String md5key;

    @NotBlank(message = "验签参数不能为空")
    private String sign;
}

package com.caih.cloud.iscs.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
@ApiModel("商户展示信息")
public class ScStoreVo {
    @ApiModelProperty("序号")
    private Integer id;
    @ApiModelProperty("登录账号")
    private String accNo;
    @ApiModelProperty("密码")
    private String pwd;
    @ApiModelProperty("姓名")
    private String userName;
    @ApiModelProperty("联系电话")
    private String phone;
    @ApiModelProperty("商户名称")
    private String storeName;
}

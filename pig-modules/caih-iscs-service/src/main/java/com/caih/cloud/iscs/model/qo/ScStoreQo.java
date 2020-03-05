package com.caih.cloud.iscs.model.qo;

import com.caih.cloud.iscs.model.base.BaseQo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
public class ScStoreQo extends BaseQo {

    private String accNo;

    private String userName;
}

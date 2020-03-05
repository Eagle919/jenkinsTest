package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.base.BaseQo;
import lombok.Data;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data

public class ScTurnoverDto extends BaseQo {

    private Integer id;

    private String dealNo;

    private String storeNo;

    private String turnoverType;

    private String turnoverAmt;

    private String accBalance;

    private String remarks;

    private String turnoverTime;

    private String storeName;
}

package com.caih.cloud.iscs.model.qo;

import com.caih.cloud.iscs.model.base.BaseQo;
import lombok.Data;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
public class ScTurnoverQo extends BaseQo {

    private String dealNo;

    private String storeNo;

    private String storeName;

    private Integer userId;

    private String startTime;

    private String endTime;
}

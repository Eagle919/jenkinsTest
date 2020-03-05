package com.caih.cloud.iscs.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
public class ScStoreFailVo {

    private String storeNo;

    private String errReson;

    public ScStoreFailVo(){
        storeNo = "";
        errReson = "";
    }
}

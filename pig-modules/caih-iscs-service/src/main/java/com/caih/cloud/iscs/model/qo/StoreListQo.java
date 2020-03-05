package com.caih.cloud.iscs.model.qo;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Data
public class StoreListQo{
    @Valid
    private List<StoreQo> storeQoList;
}

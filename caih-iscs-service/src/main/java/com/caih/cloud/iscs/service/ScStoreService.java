package com.caih.cloud.iscs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.qo.ScStoreQo;
import com.caih.cloud.iscs.model.qo.StoreListQo;
import com.caih.cloud.iscs.model.qo.StoreQo;
import com.caih.cloud.iscs.model.vo.ScStoreFailVo;
import com.caih.cloud.iscs.model.vo.ScStoreListVo;

/**
 * @author zengheng
 * @date 2019/08/05
 */
public interface ScStoreService extends IService<StoreInfo> {
    Page<StoreInfo> list(ScStoreQo qo);
    ScStoreFailVo submitStoreInfo(StoreQo qo);
    ScStoreListVo submitStoreListInfo(StoreListQo qo);
}

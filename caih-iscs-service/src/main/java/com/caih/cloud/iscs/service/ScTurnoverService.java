package com.caih.cloud.iscs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.model.entity.TurnoverInfo;
import com.caih.cloud.iscs.model.qo.ScTurnoverQo;

/**
 * @author zengheng
 * @date 2019/08/05
 */
public interface ScTurnoverService extends IService<TurnoverInfo> {
    Page<TurnoverInfo> list(ScTurnoverQo qo);
}

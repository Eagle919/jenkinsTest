package com.caih.cloud.iscs.charge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.EOrderDto;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.vo.EOrderVo;
import com.caih.cloud.iscs.charge.service.EOrderService;
import com.caih.cloud.iscs.common.CommonResult;
import com.caih.cloud.iscs.model.dto.ExtractDto;
import com.caih.cloud.iscs.model.vo.ExtractVo;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 充电桩-订单控制器
 */
@RestController
public class EOrderController extends BaseController {

    private EOrderService service;

    @Autowired
    public EOrderController(EOrderService service) {
        this.service = service;
    }


    /**
     * 列表
     *
     * @return 列表
     */
    @GetMapping("/orders")
    public CommonResult<Page<EOrderVo>> list(EOrderDto dto) {
        Page<EOrderVo> data = service.orders(dto);
        return CommonResult.success(data);
    }

}

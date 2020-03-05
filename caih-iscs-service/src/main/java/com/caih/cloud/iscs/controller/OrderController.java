package com.caih.cloud.iscs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.common.CommonResult;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.OrderDto;
import com.caih.cloud.iscs.model.vo.OrderVo;
import com.caih.cloud.iscs.service.OrderService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单相关接口
 */
@RestController
@RequestMapping("/order")
@Api(value = "/order", description = "订单接口")
public class OrderController extends BaseController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 订单列表
     *
     * @return 订单列表
     */
    @ApiOperation(value = "订单列表", httpMethod = "GET")
    @GetMapping("/list")
    public CommonResult<Page<OrderVo>> list(OrderDto qo) {
        Integer userId = this.getUserId();
        qo.setUserId(userId);
        Page<OrderVo> data = orderService.orderVos(qo);
        return CommonResult.success(data);
    }


    /**
     * 订单详情
     *
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public R<OrderVo> order(@PathVariable Integer id) {
        Pair<Boolean, OrderVo> order = orderService.order(id);
        return new R<>(order.getFirst() ? R.SUCCESS : R.FAIL, order.getSecond(), order.getFirst() ? "成功" : "失败");

    }


}

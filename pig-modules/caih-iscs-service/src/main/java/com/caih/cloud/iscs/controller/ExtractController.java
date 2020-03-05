package com.caih.cloud.iscs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.ExtractDto;
import com.caih.cloud.iscs.model.entity.Acc;
import com.caih.cloud.iscs.model.vo.ExtractVo;
import com.caih.cloud.iscs.service.ExtractService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 提现相关接口
 */
@RestController
@RequestMapping("/extract")
@Api(value = "/extract", description = "订单接口")
public class ExtractController extends BaseController {

    private ExtractService service;

    @Autowired
    public ExtractController(ExtractService service) {
        this.service = service;
    }

    @GetMapping("/list")
    @ApiOperation(value = "提现记录列表", httpMethod = "GET")
    public R<Page<ExtractVo>> list(ExtractDto dto) {
        dto.setUserId(this.getUserId());
        Page<ExtractVo> list = service.list(dto);
        return new R<>(R.SUCCESS, list, "查询成功");
    }

    @GetMapping("/balance")
    @ApiOperation(value = "账户余额", httpMethod = "GET")
    public R<String> balance() {
        Pair<Boolean, String> balance = service.getBalance(getUserId());
        return new R<>(balance.getFirst() ? R.SUCCESS : R.FAIL, balance.getSecond(), balance.getFirst() ? "查询成功" : "查询失败");
    }

    @GetMapping("/applyExtractInfo")
    @ApiOperation(value = "提现申请信息展示", httpMethod = "GET")
    public R<Acc> applyExtractInfo() {
        Acc acc = service.applyExtractInfo(this.getUserId());
        if (acc != null) {
            return new R<>(R.SUCCESS, acc, "查询成功");
        } else {
            return new R<>(R.FAIL, null, "暂无该商户账户信息");
        }
    }

    @GetMapping("/apply")
    @ApiOperation(value = "提现申请", httpMethod = "GET")
    public R<Boolean> applyExtract(ExtractDto dto) {
        if (dto.getExtractAmt() == null) return new R<>(R.FAIL, null, "请输入正确的金额");
        if (dto.getExtractAmt().compareTo(BigDecimal.ZERO) <= 0) {
            return new R<>(R.FAIL, null, "输入的金额不能为0");
        } else if (dto.getExtractAmt().compareTo(new BigDecimal(100)) < 0) {
            return new R<>(R.FAIL, null, "提现金额不能小于100元");
        } else {
            Integer userId = this.getUserId();
            dto.setUserId(userId);
            Map<Integer, Object> map = service.applyExtract(dto);
            return new R<>((Boolean) map.get(1) ? R.SUCCESS : R.FAIL, (Boolean) map.get(1), (String) map.get(2));
        }

    }

    @GetMapping("/reviewExtract")
    @ApiOperation(value = "提现审核", httpMethod = "GET")
    public R<Boolean> reviewExtract(ExtractDto dto) {
        Integer userId = this.getUserId();
        dto.setUserId(userId);
        Map<Integer, Object> map = service.reviewExtract(dto);
        return new R<>((Boolean) map.get(1) ? R.SUCCESS : R.FAIL, (Boolean) map.get(1), (String) map.get(2));
    }

}

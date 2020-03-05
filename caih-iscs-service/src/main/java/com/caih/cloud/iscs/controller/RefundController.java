package com.caih.cloud.iscs.controller;

import com.caih.cloud.iscs.common.Constants;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.TXRefundDto;
import com.caih.cloud.iscs.service.RefundService;
import com.github.pig.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.caih.cloud.iscs.common.Constants.SPLIT_EMPTY_STRING;

import com.alibaba.fastjson.JSONObject;
import com.caih.cloud.iscs.charge.utils.*;
import com.github.pig.common.util.sherry.HttpWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/deal")
@Api(value = "/deal", description = "交易接口")
public class RefundController {

    @Resource
    private RefundService refundService;

    @PostMapping("/refund")
    @ApiOperation(value = "退款", httpMethod = "POST")
    public R<Object> refund(@Valid @RequestBody TXRefundDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return new R<>(R.FAIL, SPLIT_EMPTY_STRING, bindingResult.getFieldError().getDefaultMessage());
        Pair<String, Object> refund = refundService.refund(dto);
        String first = refund.getFirst();
        if (Constants.DEAL_FAIL.equals(first)) return new R<>(R.SUCCESS, refund.getSecond(), "操作成功");
        else if (Constants.DEAL_FAIL_TX.equals(first)) return new R<>(R.FAIL, refund.getSecond(), "操作失败");
        else if (Constants.DEAL_SUCCESS.equals(first)) return new R<>(R.SUCCESS, refund.getSecond(), "操作成功");
        else return new R<>(R.FAIL, refund.getSecond(), "操作失败");
    }

}

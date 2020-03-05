package com.caih.cloud.iscs.controller;

import com.caih.cloud.iscs.model.qo.ScTurnoverQo;
import com.caih.cloud.iscs.service.ScTurnoverService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.caih.cloud.iscs.common.Constants.QUERY_SUCCESS;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Api(value = "/turnover", description = "流水列表")
@RestController
@RequestMapping("/turnover")
public class ScTurnoverController extends BaseController {

    @Autowired
    private ScTurnoverService scTurnoverService;

    @ApiOperation(value = "流水信息列表", httpMethod = "GET")
    @GetMapping("/list")
    public R list(ScTurnoverQo qo) {
        Integer userId = this.getUserId();
        qo.setUserId(userId);
//        qo.setUserId(5);
        return new R(R.SUCCESS, scTurnoverService.list(qo), QUERY_SUCCESS);
    }


}

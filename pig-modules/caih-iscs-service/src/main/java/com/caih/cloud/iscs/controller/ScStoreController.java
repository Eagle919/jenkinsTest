package com.caih.cloud.iscs.controller;

import com.caih.cloud.iscs.common.AllInPayUtil;
import com.caih.cloud.iscs.common.IscsUtil;
import com.caih.cloud.iscs.model.qo.ScStoreQo;
import com.caih.cloud.iscs.model.qo.StoreListQo;
import com.caih.cloud.iscs.model.qo.StoreQo;
import com.caih.cloud.iscs.model.vo.ScStoreFailVo;
import com.caih.cloud.iscs.model.vo.ScStoreListVo;
import com.caih.cloud.iscs.service.ScStoreService;
import com.github.pig.common.util.R;
import com.github.pig.common.web.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.caih.cloud.iscs.common.Constants.*;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Api(value = "/store", description = "商户列表")
@RestController
@RequestMapping("/")
public class ScStoreController extends BaseController {

    @Value("${order.secret}")
    private String orderSecret;

    @Autowired
    private ScStoreService scStoreService;

    @ApiOperation(value = "商户信息列表", httpMethod = "GET")
    @GetMapping("store/list")
    public R list(ScStoreQo qo) {
        return new R(R.SUCCESS, scStoreService.list(qo), QUERY_SUCCESS);
    }

    @ApiOperation(value = "同步商户信息", httpMethod = "POST")
    @PostMapping("api/submitStoreInfo")
    public R submitStoreInfo(@RequestBody @Valid StoreQo qo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new R(R.FAIL,SPLIT_EMPTY_STRING,bindingResult.getFieldError().getDefaultMessage());
        }

        Map treeMap = new TreeMap();
        try {
            IscsUtil.copyObjectToMap(qo, treeMap);
        }catch (Exception e){
            return new R(R.FAIL, SPLIT_EMPTY_STRING, e.getMessage());
        }
        //验签
        Boolean signFlag = AllInPayUtil.validsign(treeMap, orderSecret);
        if(!signFlag){
            return new R(R.SUCCESS, SPLIT_EMPTY_STRING, SIGN_FAIL);
        }
        ScStoreFailVo scStoreFailVo = scStoreService.submitStoreInfo(qo);
        if(!scStoreFailVo.getStoreNo().equals(SPLIT_EMPTY_STRING)){
            return new R(R.FAIL, scStoreFailVo, STORE_SUBMIT_FAIL);
        }else {
            return new R(R.SUCCESS, SPLIT_EMPTY_STRING, STORE_SUBMIT_SUCCESS);
        }
    }

    @ApiOperation(value = "同步商户信息", httpMethod = "POST")
    @PostMapping("api/submitStoreListInfo")
    public R submitStoreListInfo(@RequestBody @Valid StoreListQo qo , BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new R(R.FAIL,SPLIT_EMPTY_STRING,bindingResult.getFieldError().getDefaultMessage());
        }
        ScStoreListVo scStoreListVo = scStoreService.submitStoreListInfo(qo);
        List<ScStoreFailVo> result = scStoreListVo.getStoreNoList();
        if(result.isEmpty()){
            return new R(R.SUCCESS, SPLIT_EMPTY_STRING, STORE_SUBMIT_SUCCESS);
        }else {
            return new R(R.FAIL, result, STORE_SUBMIT_FAIL);
        }

    }

}

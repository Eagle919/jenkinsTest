package com.caih.cloud.iscs.charge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.EMemberDto;
import com.caih.cloud.iscs.charge.model.entity.EMember;
import com.caih.cloud.iscs.charge.model.vo.EMemberVo;
import com.caih.cloud.iscs.charge.service.EMemberService;
import com.caih.cloud.iscs.common.CommonResult;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 充电桩-会员控制器
 */
@RestController
public class EMemberController extends BaseController {

    private EMemberService service;

    @Autowired
    public EMemberController(EMemberService service) {
        this.service = service;
    }


    /**
     * 列表
     *
     * @return 列表
     */
    @GetMapping("/members")
    public CommonResult<Page<EMemberVo>> list(EMemberDto dto) {
        Page<EMemberVo> data = service.members(dto);
        return CommonResult.success(data);
    }

}

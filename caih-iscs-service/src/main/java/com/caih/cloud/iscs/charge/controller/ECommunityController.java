package com.caih.cloud.iscs.charge.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.ECommunityDto;
import com.caih.cloud.iscs.charge.model.dto.ESendMessageDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.vo.ECommunityVo;
import com.caih.cloud.iscs.charge.scoket.DeviceHandler;
import com.caih.cloud.iscs.charge.scoket.DeviceStatus;
import com.caih.cloud.iscs.charge.service.ECommunityService;
import com.caih.cloud.iscs.charge.service.EDeviceService;
import com.caih.cloud.iscs.charge.service.EOrderService;
import com.caih.cloud.iscs.charge.service.ESocketService;
import com.caih.cloud.iscs.charge.utils.EStringUtils;
import com.caih.cloud.iscs.common.CommonResult;
import com.caih.cloud.iscs.common.DateUtils;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 充电桩-社区控制器
 */
@RestController
public class ECommunityController extends BaseController {

    private ECommunityService service;

    @Resource
    private EDeviceService deviceService;

    @Autowired
    public ECommunityController(ECommunityService service) {
        this.service = service;
    }


    /**
     * 列表
     *
     * @return 列表
     */
    @GetMapping("/communitys")
    public CommonResult<Page<ECommunityVo>> list(ECommunityDto dto) {
        Page<ECommunityVo> data = service.communitys(dto);
        return CommonResult.success(data);
    }

    /**
     * 添加
     *
     * @param dto 社区信息信息
     * @return success/false
     */
    @PostMapping("/community/add")
    public CommonResult<Object> add(@RequestBody ECommunity dto) {

        if (EStringUtils.isEmpty(dto.getName())) return CommonResult.validateFailed("参数:Name不能为空");
        if (EStringUtils.isEmpty(dto.getAddress())) return CommonResult.validateFailed("参数:Address不能为空");
        if (!EStringUtils.isNumeric(dto.getRate()) && !EStringUtils.isDouble(dto.getRate()))
            return CommonResult.validateFailed("参数Rate:不能为空且为数字");
        if (StringUtils.isEmpty(dto.getRemark())) return CommonResult.validateFailed("参数:Remark不能为空");
        dto.setCommunityNo(DateUtils.getDateNo());

//        QueryWrapper<ECommunity> cqw = new QueryWrapper<>();
//        cqw.eq("name", dto.getName());
//        cqw.or();
//        cqw.eq("address", dto.getAddress());
//        int count = service.count(cqw);
//        if (count > 0) return CommonResult.validateFailed("小区名字或者地址重复");

        return CommonResult.success(service.save(dto));
    }

    /**
     * 删除
     *
     * @param dto 社区信息信息
     * @return success/false
     */
    @GetMapping("/community/del")
    public CommonResult<Boolean> del(ECommunityDto dto) {

        if (!EStringUtils.isNumeric(dto.getCommunityId().toString())) return CommonResult.validateFailed("参数:CommunityId不是数字");



        ECommunity community = service.getById(dto.getCommunityId());

        if (community == null) return CommonResult.success(false, "ID为【" + dto.getCommunityId() + "】的社区不存在");

        QueryWrapper<EDevice> dq = new QueryWrapper<>();
        dq.eq("community_id", dto.getCommunityId());
        List<EDevice> devices = deviceService.list(dq);

        if (devices.size() > 0) return CommonResult.success(false, "请先删除设备,再删除小区");

        service.removeById(dto.getCommunityId());

        return CommonResult.success(true);
    }

    /**
     * 更新-信息回显
     *
     * @return success/false
     */
    @GetMapping("/community/update/view")
    public CommonResult<ECommunityVo> view(ECommunityDto dto) {

        if (!EStringUtils.isNumeric(dto.getCommunityId().toString())) return CommonResult.validateFailed("参数:CommunityId不是数字");

        ECommunity community = service.getById(dto.getCommunityId());

        ECommunityVo vo = new ECommunityVo();
        vo.setCommunityId(dto.getCommunityId());
        vo.setName(community.getName());
        vo.setAddress(community.getAddress());
        vo.setRate(community.getRate());
        vo.setRemark(community.getRemark());

        return CommonResult.success(vo);
    }

    /**
     * 更新
     *
     * @param dto 社区信息信息
     * @return success/false
     */
    @PostMapping("/community/update")
    public CommonResult<Boolean> update(@RequestBody ECommunityDto dto) {

        if (!EStringUtils.isNumeric(dto.getCommunityId().toString()))
            return CommonResult.validateFailed("参数:CommunityId不是数字");
        if (EStringUtils.isEmpty(dto.getName())) return CommonResult.validateFailed("参数:name不能为空");
        if (EStringUtils.isEmpty(dto.getAddress())) return CommonResult.validateFailed("参数:address不能为空");
        if (EStringUtils.isEmpty(dto.getRemark())) return CommonResult.validateFailed("参数:remark不能为空");
        if (!EStringUtils.isNumeric(dto.getRate()) && !EStringUtils.isDouble(dto.getRate()))
            return CommonResult.validateFailed("参数:rate不能为空或非数字");


        ECommunity community = service.getById(dto.getCommunityId());

        if (community == null) return CommonResult.success(false, "ID为:" + dto.getCommunityId() + "的社区不存在");

//        QueryWrapper<ECommunity> cqw = new QueryWrapper<>();
//        cqw.eq("name", dto.getName());
//        cqw.or();
//        cqw.eq("address", dto.getAddress());
//        int count = service.count(cqw);
//        if (count > 0) {
//            if (!community.getName().equals(dto.getName()) || !community.getAddress().equals(dto.getAddress()))
//                return CommonResult.success(false, "小区名字或者地址重复");
//        }
        community.setName(dto.getName());
        community.setAddress(dto.getAddress());
        community.setRate(dto.getRate());
        community.setRemark(dto.getRemark());

        return CommonResult.success(service.updateById(community));
    }

    /**
     * 开始充电
     *
     * @param dto
     * @return
     */
    @GetMapping("/sendStartChargeMessage")
    public CommonResult<Object> sendMessage(ESendMessageDto dto) {
        try {
            DeviceHandler.sendStartChargeMessage(dto.getDeviceCode(), dto.getTimer(), dto.getPort());
            return CommonResult.success("充电成功");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.success(e.getMessage());
        }

    }

    @GetMapping("/sendStopChargeMessage")
    public CommonResult<Object> stopMessage(ESendMessageDto dto) {
        try {
            DeviceHandler.sendStopChargeMessage(dto.getDeviceCode(), dto.getPort());
            return CommonResult.success("发送成功");

        } catch (Exception e) {
            return CommonResult.success(e.getMessage());
        }

    }

    @GetMapping("/getDeviceStatus")
    public CommonResult<Object> getDeviceStatus(ESendMessageDto dto) {

        List<DeviceStatus> deviceStatusList = new ArrayList<>();
        try {
            deviceStatusList = DeviceHandler.getDeviceStatusByCode(dto.getDeviceCode());
            return CommonResult.success(deviceStatusList);
        } catch (Exception e) {
            return CommonResult.success(e.getMessage());
        }

    }


}

package com.caih.cloud.iscs.charge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.charge.model.dto.EDeviceDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.entity.EOrder;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import com.caih.cloud.iscs.charge.model.vo.EDeviceVo;
import com.caih.cloud.iscs.charge.service.ECommunityService;
import com.caih.cloud.iscs.charge.service.EDeviceService;
import com.caih.cloud.iscs.charge.service.EOrderService;
import com.caih.cloud.iscs.charge.service.ESocketService;
import com.caih.cloud.iscs.charge.utils.EStringUtils;
import com.caih.cloud.iscs.common.CommonResult;
import com.github.pig.common.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 充电桩-设备管理控制器
 */
@RestController
public class EDeviceController extends BaseController {

    private EDeviceService service;

    @Resource
    private ESocketService socketService;

    @Resource
    private ECommunityService communityService;

    @Resource
    private EOrderService orderService;

    @Autowired
    public EDeviceController(EDeviceService service) {
        this.service = service;
    }


    @GetMapping("/devices")
    public CommonResult<Page<EDeviceVo>> devices(EDeviceDto dto) {
        Page<EDeviceVo> data = service.devices(dto);
        return CommonResult.success(data);
    }

    /**
     * 添加
     *
     * @param dto 设备信息
     * @return success/false
     */
    @PostMapping("/device/add")
    public CommonResult<Boolean> add(@RequestBody EDeviceDto dto) {

        if (!EStringUtils.isNumeric(dto.getDeviceNo())) return CommonResult.validateFailed("参数:DeviceNo不是数字");
        if (!EStringUtils.isNumeric(dto.getCommunityId().toString()))
            return CommonResult.validateFailed("参数:CommunityId不是数字");

        if (isExistDeviceNo(dto)) return CommonResult.success(false, "编号:" + dto.getDeviceNo() + "的设备已存在");

        ECommunity community = communityService.getById(dto.getCommunityId());
        //现有小区
        if (community == null) return CommonResult.success(false, "ID:" + dto.getCommunityId() + "的社区不存在");
        EDevice device = new EDevice();
        device.setDeviceNo(dto.getDeviceNo());
        device.setCommunityId(dto.getCommunityId());
        device.setOnline(2);
        device.setCreateTime(new Date());
        device.setDel(0);
        device.setDeviceNo(dto.getDeviceNo());

        QueryWrapper<ESocket> sqw = new QueryWrapper<>();
        sqw.eq("device_no", dto.getDeviceNo());
        Integer socketCount = socketService.getBaseMapper().selectCount(sqw);
        if (socketCount == 0) {
            //一并添加10个插座信息
            for (int i = 0; i < 10; i++) {
                ESocket socket = new ESocket();
                socket.setDel(0);
                socket.setCreateTime(new Date());
                socket.setDeviceNo(device.getDeviceNo());
                socket.setPortNo(i);
                socket.setStatus(1);

                socketService.save(socket);
            }
        }
        return CommonResult.success(service.save(device));
    }

    /**
     * 删除
     *
     * @param dto 设备信息
     * @return success/false
     */
    @GetMapping("/device/del")
    public CommonResult<Boolean> del(EDeviceDto dto) {

        if (!EStringUtils.isNumeric(dto.getDeviceNo())) return CommonResult.validateFailed("参数:DeviceNo不是数字");

        QueryWrapper<EDevice> deviceQueryWrapper = new QueryWrapper<>();
        deviceQueryWrapper.eq("device_no", dto.getDeviceNo());
        EDevice device = service.getOne(deviceQueryWrapper);

        if (device == null) return CommonResult.success(false, "设备编号为【" + dto.getDeviceNo() + "】的充电桩不存在");

        //移除device对应的socket
        QueryWrapper<ESocket> socketQueryWrapper = new QueryWrapper<>();
        socketQueryWrapper.eq("device_no", device.getDeviceNo());
        List<ESocket> sockets = socketService.list(socketQueryWrapper);
        for (ESocket socket : sockets) {
            //校验订单里状态为正在充电的插座是否有
            QueryWrapper<EOrder> oqw = new QueryWrapper<>();
            oqw.eq("socket_id", socket.getId());
            oqw.eq("status", 1); //正在充电 不能删除设备
            List<EOrder> orders = orderService.list(oqw);
            if (orders.size() > 0) return CommonResult.success(false, "设备编号为【" + dto.getDeviceNo() + "】的充电桩还有正在工作的插座");
        }
        socketService.remove(socketQueryWrapper);

        return CommonResult.success(service.remove(deviceQueryWrapper));
    }

    /**
     * 修改-回显
     *
     * @param dto 设备信息
     * @return EDevice
     */
    @PostMapping("/device/update/view")
    public CommonResult<EDevice> view(@RequestBody EDeviceDto dto) {

        if (!EStringUtils.isNumeric(dto.getId().toString())) return CommonResult.validateFailed("参数:Id不能为空且为数字");

        return CommonResult.success(service.getById(dto.getId()));
    }

    /**
     * 修改
     *
     * @param dto 设备信息
     * @return success/false
     */
    @PostMapping("/device/update")
    public CommonResult<Boolean> update(@RequestBody EDeviceDto dto) {

        if (!EStringUtils.isNumeric(dto.getId().toString())) return CommonResult.validateFailed("参数:设备Id不是数字");
        if (!EStringUtils.isNumeric(dto.getCommunityId().toString()))
            return CommonResult.validateFailed("参数:communityId不是数字");
        if (!EStringUtils.isNumeric(dto.getDeviceNo())) return CommonResult.validateFailed("参数:deviceNo不能为空或非数字");

        EDevice device = service.getById(dto.getId());
        if (device == null) return CommonResult.success(false, "ID为:" + dto.getCommunityId() + "的设备不存在");
        ECommunity community = communityService.getById(dto.getCommunityId());
        if (community == null) return CommonResult.success(false, "ID为【" + dto.getCommunityId() + "】的小区为空");

        QueryWrapper<EDevice> eqw = new QueryWrapper<>();
        eqw.eq("device_no", dto.getDeviceNo());
        int count = service.count(eqw);
        if (count > 0) {
            if (!device.getDeviceNo().equals(dto.getDeviceNo()))
                return CommonResult.success(false, "设备编号重复");
        }
        device.setUpdateTime(new Date());
        device.setDeviceNo(dto.getDeviceNo());
        device.setCommunityId(dto.getCommunityId());


        //update socket：device no
        QueryWrapper<ESocket> socketQueryWrapper = new QueryWrapper<>();
        socketQueryWrapper.eq("device_no", device.getDeviceNo());
        List<ESocket> sockets = socketService.list(socketQueryWrapper);
        for (ESocket socket : sockets) {
            socket.setUpdateTime(new Date());
            socket.setDeviceNo(dto.getDeviceNo());
            socketService.updateById(socket);
        }

        return CommonResult.success(service.updateById(device));
    }


    private boolean isExistDeviceNo(EDeviceDto dto) {
        if (!EStringUtils.isNumeric(dto.getDeviceNo())) return false;
        QueryWrapper<EDevice> dqw = new QueryWrapper<>();
        dqw.eq("device_no", dto.getDeviceNo());
        Integer deviceCount = service.getBaseMapper().selectCount(dqw);
        return deviceCount != 0;
    }

}

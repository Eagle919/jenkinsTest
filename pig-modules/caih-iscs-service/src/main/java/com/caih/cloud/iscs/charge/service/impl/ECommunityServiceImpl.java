package com.caih.cloud.iscs.charge.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.charge.mapper.ECommunityMapper;
import com.caih.cloud.iscs.charge.mapper.EDeviceMapper;
import com.caih.cloud.iscs.charge.model.dto.ECommunityDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import com.caih.cloud.iscs.charge.model.vo.ECommunityVo;
import com.caih.cloud.iscs.charge.service.ECommunityService;
import com.caih.cloud.iscs.charge.service.EDeviceService;
import com.caih.cloud.iscs.charge.service.ESocketService;
import com.caih.cloud.iscs.charge.service.SmartCommunityService;
import com.caih.cloud.iscs.common.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ECommunityServiceImpl extends ServiceImpl<ECommunityMapper, ECommunity> implements ECommunityService {


    @Resource
    private ECommunityMapper mapper;

    @Resource
    private EDeviceMapper deviceMapper;

    @Resource
    private ESocketService socketService;

    @Resource
    private SmartCommunityService smartCommunityService;

    @Override
    public Page<ECommunityVo> communitys(ECommunityDto dto) {

        Page<ECommunityVo> page = new Page<>(dto.getPage(), dto.getLimit());
        List<ECommunityVo> eCommunityVos = mapper.communitys(page, dto);
        List<ECommunityVo> results = new ArrayList<>();
        for (ECommunityVo vo : eCommunityVos) {
            QueryWrapper<EDevice> dqw = new QueryWrapper<>();
            dqw.eq("community_id", vo.getCommunityId());
            List<EDevice> devices = deviceMapper.selectList(dqw);
            for (EDevice device : devices) {
                List deviceStatus = smartCommunityService.getDeviceStatus(device.getDeviceNo());
                if (deviceStatus == null || deviceStatus.size() == 0) { //离线
                    device.setOnline(2);
                } else {//在线
                    device.setOnline(1);
                }
                device.setUpdateTime(new Date());
                deviceMapper.updateById(device);
            }
            vo.setCountNumber(deviceMapper.countDeviceNumber(vo.getCommunityId()));
            vo.setOnlineCount(deviceMapper.countOnlineDeviceNumber(vo.getCommunityId()));
            results.add(vo);
        }
        return page.setRecords(results);
    }
}

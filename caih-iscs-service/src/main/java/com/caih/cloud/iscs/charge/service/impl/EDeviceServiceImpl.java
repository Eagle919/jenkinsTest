package com.caih.cloud.iscs.charge.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.charge.mapper.ECommunityMapper;
import com.caih.cloud.iscs.charge.mapper.EDeviceMapper;
import com.caih.cloud.iscs.charge.mapper.ESocketMapper;
import com.caih.cloud.iscs.charge.model.dto.EDeviceDto;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EDevice;
import com.caih.cloud.iscs.charge.model.entity.ESocket;
import com.caih.cloud.iscs.charge.model.vo.EDeviceVo;
import com.caih.cloud.iscs.charge.model.vo.ESocketVo;
import com.caih.cloud.iscs.charge.service.EDeviceService;
import com.caih.cloud.iscs.charge.service.SmartCommunityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EDeviceServiceImpl extends ServiceImpl<EDeviceMapper, EDevice> implements EDeviceService {

    @Resource
    private EDeviceMapper deviceMapper;

    @Resource
    private ECommunityMapper communityMapper;

    @Resource
    private ESocketMapper socketMapper;

    @Resource
    private SmartCommunityService smartCommunityService;

    @Override
    public Page<EDeviceVo> devices(EDeviceDto dto) {

        Page<EDeviceVo> page = new Page<>(dto.getPage(), dto.getLimit());

        List<EDeviceVo> devices = deviceMapper.devices(page, dto);
        List<EDeviceVo> results = new ArrayList<>();
        for (EDeviceVo device : devices) {
            //更新设备在线状态
            EDevice d = deviceMapper.selectById(device.getId());
            List deviceStatus = smartCommunityService.getDeviceStatus(device.getDeviceNo());
            if (deviceStatus == null || deviceStatus.size() == 0) { //离线
                d.setOnline(2);
                device.setOnline(2);
            } else {//在线
                d.setOnline(1);
                device.setOnline(1);
                //update socket
                QueryWrapper<ESocket> sq = new QueryWrapper<>();
                sq.eq("device_no", d.getDeviceNo());
                List<ESocket> sockets = socketMapper.selectList(sq);
                for (ESocket socket : sockets) {
                    for (Object portAndStatus : deviceStatus) {
                        Map m = JSON.parseObject(portAndStatus.toString(), Map.class);
                        if (socket.getPortNo().equals(m.get("port"))) {
                            socket.setStatus(Integer.parseInt(m.get("status").toString()));
                            socket.setUpdateTime(new Date());
                            socketMapper.updateById(socket);
                            break;
                        }
                    }
                }
            }
            d.setUpdateTime(new Date());
            deviceMapper.updateById(d);

            ECommunity community = communityMapper.selectById(device.getCommunityId());
            if (community == null) continue;
            device.setCommunityName(community.getName());
            device.setCommunityRate(new BigDecimal(community.getRate()));
            QueryWrapper<ESocket> sq = new QueryWrapper<>();
            sq.eq("device_no", device.getDeviceNo());
            List<ESocket> sockets = socketMapper.selectList(sq);
            device.setTotalSocketNumber(sockets.size());
            sq.eq("status", 1);
            device.setFreeSocketNumber(socketMapper.selectList(sq).size());
            results.add(device);
        }
        return page.setRecords(results);
    }


}

package com.caih.cloud.iscs.charge.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.charge.mapper.*;
import com.caih.cloud.iscs.charge.model.dto.EOrderDto;
import com.caih.cloud.iscs.charge.model.entity.*;
import com.caih.cloud.iscs.charge.model.vo.EOrderVo;
import com.caih.cloud.iscs.charge.service.EOrderService;
import com.caih.cloud.iscs.charge.utils.TimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class EOrderServiceImpl extends ServiceImpl<EOrderMapper, EOrder> implements EOrderService {

    @Resource
    private EOrderMapper eOrderMapper;

    @Resource
    private EDeviceMapper deviceMapper;

    @Resource
    private ECommunityMapper communityMapper;

    @Resource
    private EMemberMapper memberMapper;

    @Resource
    private ESocketMapper socketMapper;

    @Override
    public Page<EOrderVo> orders(EOrderDto dto) {

        Page<EOrderVo> pageVo = new Page<>(dto.getPage(), dto.getLimit());

        Page<EOrder> page = new Page<>(dto.getPage(), dto.getLimit());

        QueryWrapper<EOrder> qo = new QueryWrapper<>();
        if (dto.getOrderNo() != null)
            qo.like("order_no", dto.getOrderNo());
        if (dto.getStatus() != null)
            qo.like("status", dto.getStatus());
        qo.orderByDesc("create_time");

        IPage<EOrder> orders = eOrderMapper.selectPage(page, qo);

        ArrayList<EOrderVo> data = new ArrayList<>();

        for (EOrder order : orders.getRecords()) {

            EOrderVo vo = new EOrderVo();
            vo.setCreateTime(order.getCreateTime());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getStatus());

            EDevice device = deviceMapper.selectById(order.getDeviceId());
            vo.setDeviceNO(device == null ? "" : device.getDeviceNo());

            ESocket socket = socketMapper.selectById(order.getSocketId());
            String timeDiffer = "0";
            if (socket != null) {
                if (socket.getStartTime() != null || socket.getEndTime() != null) {
                    timeDiffer = TimeUtils.timeDiffer(socket.getEndTime(), socket.getStartTime());
                    vo.setChargeTime(timeDiffer);
                }
            }

            ECommunity community = communityMapper.selectById(device == null ? "" : device.getCommunityId());
            vo.setCommunityName(community == null ? "" : community.getName());
            vo.setPay(order.getPay().toString());
            if (community != null) {
                String rate = community.getRate();
                if (rate != null) {
                    BigDecimal num1 = new BigDecimal(timeDiffer);
                    BigDecimal pay = num1.multiply(new BigDecimal(rate));
                    vo.setActualPay(pay.toString() == null ? "0" : pay.toString());
                }
            }

//            vo.setRefund(order.getPay().subtract(new BigDecimal(vo.getActualPay())).toString());
            if (order.getRefund() == null) {
                vo.setRefund("0.00");
            } else {
                vo.setRefund(order.getRefund().toString());
            }

            EMember member = memberMapper.selectById(order.getMemberId());
            vo.setMemberName(member == null ? "" : member.getName());
            if (vo.getChargeTime() == null) {
                vo.setChargeTime("0");
            }
            if (vo.getActualPay() == null) {
                vo.setActualPay("0");
            }
            data.add(vo);

        }

        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        pageVo.setRecords(data);

        return pageVo.setRecords(data);
    }
}

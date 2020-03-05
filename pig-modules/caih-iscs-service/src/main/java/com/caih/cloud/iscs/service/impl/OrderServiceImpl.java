package com.caih.cloud.iscs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.common.Constants;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.feign.UserFeignService;
import com.caih.cloud.iscs.mapper.AccMapper;
import com.caih.cloud.iscs.mapper.ExpressInfoMapper;
import com.caih.cloud.iscs.mapper.GoodInfoMapper;
import com.caih.cloud.iscs.mapper.OrderMapper;
import com.caih.cloud.iscs.model.dto.OrderDto;
import com.caih.cloud.iscs.model.entity.Acc;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import com.caih.cloud.iscs.model.vo.GoodInfoVo;
import com.caih.cloud.iscs.model.vo.OrderVo;
import com.caih.cloud.iscs.service.OrderService;
import com.github.pig.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    private OrderMapper orderMapper;
    private GoodInfoMapper goodInfoMapper;
    private AccMapper accMapper;
    private ExpressInfoMapper expressInfoMapper;
    private UserFeignService userFeignService;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, GoodInfoMapper goodInfoMapper, AccMapper accMapper,
                            ExpressInfoMapper expressInfoMapper, UserFeignService userFeignService) {
        this.orderMapper = orderMapper;
        this.goodInfoMapper = goodInfoMapper;
        this.accMapper = accMapper;
        this.expressInfoMapper = expressInfoMapper;
        this.userFeignService = userFeignService;
    }

    @Override
    public Page<OrderVo> orderVos(OrderDto qo) {
        Page<OrderVo> page = new Page<>(qo.getPage(), qo.getLimit());
        UserVO userVO = userFeignService.user(qo.getUserId());
        if (userVO.getDeptId() == Constants.STORE_TYPE) {
            QueryWrapper<Acc> qw = new QueryWrapper<>();
            qw.eq("acc_no", userVO.getUsername());
            Acc acc = accMapper.selectOne(qw);
            qo.setStoreNo(acc.getStoreNo());
        }
        List<OrderVo> orders = orderMapper.orders(page, qo);
        //封装订单里的商品信息
        for (OrderVo vo : orders) {
            if (vo.getExpressComNo() == null || "".equals(vo.getExpressComNo())) {
                vo.setExpressComName("");
            } else {
                String expressName = expressInfoMapper.getExpressName(vo.getExpressComNo());
                vo.setExpressComName(expressName);
            }
            List<GoodInfoVo> goodInfoVos = goodInfoMapper.goodInfos(vo.getOrderNo());
            //设置订单总金额
            vo.setOrderAmt(calcTotalAmount(goodInfoVos, vo));
            //设置订单商品
            vo.setGoods(goodInfoVos);
        }
        return page.setRecords(orders);
    }

    //计算总金额
    private String calcTotalAmount(List<GoodInfoVo> goodInfoVos, OrderVo vo) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
        BigDecimal postageDecimal = new BigDecimal(vo.getPostage());
        BigDecimal totalAmount = new BigDecimal("0");
        for (GoodInfoVo item : goodInfoVos) {
            totalAmount = totalAmount.add(new BigDecimal(item.getGoodsPrice()).multiply(new BigDecimal(item.getGoodsNum())));
            item.setGoodsPrice(decimalFormat.format(new BigDecimal(item.getGoodsPrice())));
        }
        //格式化邮费展示
        if ("0".equals(vo.getPostage())) {
            vo.setPostage("(包邮产品)");
        } else {
            vo.setPostage("(含邮费:" + decimalFormat.format(new BigDecimal(vo.getPostage())) + ")");
        }
        return decimalFormat.format(totalAmount.add(postageDecimal));
    }

    @Override
    public Pair<Boolean, OrderVo> order(Integer id) {
        OrderVo order = orderMapper.order(id);
        if (order == null) return new Pair<>(false, null);
        if (order.getOrderNo() == null) return new Pair<>(false, null);
        List<GoodInfoVo> goodInfoVos = goodInfoMapper.goodInfos(order.getOrderNo());
        if (order.getExpressComNo() != null) {
            String expressName = expressInfoMapper.getExpressName(order.getExpressComNo());
            if (expressName != null) {
                order.setExpressComName(expressName);
            }
        }
        order.setGoods(goodInfoVos);
        return new Pair<>(true, order);
    }
}

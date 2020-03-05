package com.caih.cloud.iscs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.ExtractDto;
import com.caih.cloud.iscs.model.entity.Acc;
import com.caih.cloud.iscs.model.entity.Extract;
import com.caih.cloud.iscs.model.vo.ExtractVo;

import java.util.Map;

public interface ExtractService extends IService<Extract> {

    /**
     * 根据用户id获取商户余额
     *
     * @param userId 用户id
     * @return 格式化后的String类型余额
     */
    String balance(Integer userId);

    /**
     * 获取提现记录列表
     *
     * @param dto 前端查询条件封装类
     * @return 提现记录
     */
    Page<ExtractVo> list(ExtractDto dto);

    /**
     * 点击提现申请后的页面信息展示
     *
     * @param userId 用户id
     * @return 根据用户id判断是否可以进行提现申请
     */
    Acc applyExtractInfo(Integer userId);

    /**
     * 提现申请
     *
     * @param dto 前端查询条件封装类
     * @return 是否提现成功，并给出相关提示
     */
    Map<Integer, Object> applyExtract(ExtractDto dto);

    /**
     * 平台端对提现进行审核接口
     *
     * @param dto 提现前端传来的参数实体
     * @return 审核后的结果及相应提示
     */
    Map<Integer,Object> reviewExtract(ExtractDto dto);


    Pair<Boolean,String> getBalance(Integer userId);
}

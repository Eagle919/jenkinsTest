package com.caih.cloud.iscs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.common.Constants;
import com.caih.cloud.iscs.feign.UserFeignService;
import com.caih.cloud.iscs.mapper.ScStoreMapper;
import com.caih.cloud.iscs.mapper.ScTurnoverMapper;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.entity.TurnoverInfo;
import com.caih.cloud.iscs.model.qo.ScTurnoverQo;
import com.caih.cloud.iscs.service.ScTurnoverService;
import com.github.pig.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author zengheng
 * @date 2019/08/05
 */
@Service
public class ScTurnoverServiceImpl extends ServiceImpl<ScTurnoverMapper, TurnoverInfo> implements ScTurnoverService {

    @Autowired
    private ScStoreMapper scStoreMapper;

    @Autowired
    private UserFeignService userFeignService;

    @Override
    public Page<TurnoverInfo> list(ScTurnoverQo qo) {
        Page page = new Page(qo.getPage(), qo.getLimit());
        UserVO userVO = userFeignService.user(qo.getUserId());
        if(userVO.getDeptId() == Constants.STORE_TYPE){
            QueryWrapper<StoreInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("acc_no", userVO.getUsername());
            qo.setStoreNo(scStoreMapper.selectOne(queryWrapper).getStoreNo());
        }
        List<TurnoverInfo> list = baseMapper.list(page, qo);
        return page.setRecords(list);
    }
}

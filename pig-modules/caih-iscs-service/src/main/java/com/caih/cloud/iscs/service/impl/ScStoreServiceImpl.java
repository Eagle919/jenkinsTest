package com.caih.cloud.iscs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.mapper.ScStoreMapper;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.qo.ScStoreQo;
import com.caih.cloud.iscs.model.qo.StoreListQo;
import com.caih.cloud.iscs.model.qo.StoreQo;
import com.caih.cloud.iscs.model.vo.ScStoreFailVo;
import com.caih.cloud.iscs.model.vo.ScStoreListVo;
import com.caih.cloud.iscs.service.ScStoreService;
import com.github.pig.common.util.RegUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.caih.cloud.iscs.common.Constants.*;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Service
public class ScStoreServiceImpl extends ServiceImpl<ScStoreMapper, StoreInfo> implements ScStoreService {

    @Autowired
    private ScStoreMapper scStoreMapper;

    @Override
    public Page<StoreInfo> list(ScStoreQo qo) {
        Page p = new Page(qo.getPage(), qo.getLimit());
        return p.setRecords(baseMapper.list(p, qo));
    }

    @Override
    public ScStoreFailVo submitStoreInfo(StoreQo qo) {

        //手机号正则校验
        Assert.isTrue(RegUtils.validateTel(qo.getPhone()),"手机格式错误");

        ScStoreFailVo scStoreFailVo = new ScStoreFailVo();
        String errReason = errReasonString(errReasonList(qo));
        String storeNo = qo.getStoreNo();
        if(errReason.length() > LENGTH_0){
            scStoreFailVo.setStoreNo(storeNo);
            scStoreFailVo.setErrReson(errReason);
        }else {
            if(saveToStoreInfoTable(qo)){
                saveToUserTable(qo);
            }else {
                scStoreFailVo.setStoreNo(storeNo);
                scStoreFailVo.setErrReson(QUERY_FAIL_SERVICE);
            }
        }

        return scStoreFailVo;
    }

    @Override
    public ScStoreListVo submitStoreListInfo(StoreListQo qo) {
        ScStoreListVo scStoreListVo = new ScStoreListVo();
        List<StoreQo> storeQoList = qo.getStoreQoList();
        List<ScStoreFailVo> failList = new ArrayList<>();
        for(StoreQo m : storeQoList){
            String errReason = errReasonString(errReasonList(m));
            String storeNo = m.getStoreNo();
            if(errReason.length() > LENGTH_0){
                failList.add(createFailVo(errReason, storeNo));
            }else {
                if(saveToStoreInfoTable(m)){
                    saveToUserTable(m);
                }else {
                    failList.add(createFailVo(QUERY_FAIL_SERVICE, storeNo));
                }
            }
        }
        scStoreListVo.setStoreNoList(failList);
        return scStoreListVo;
    }

    private Date getCurrentTime(){
        Date date = new Date();
        return date;
    }

    private ScStoreFailVo createFailVo(String reason, String value){
        ScStoreFailVo storeFailVo = new ScStoreFailVo();
        storeFailVo.setErrReson(reason);
        storeFailVo.setStoreNo(value);
        return storeFailVo;
    }

    /**
     * 生成最后的错误原因
     *
     */
    private String errReasonString(List<String> errReasonList){
        String errReasonString = "";
        if(!errReasonList.isEmpty()){
            for(String m : errReasonList){
                errReasonString += m + SPLIT_BALNK_COMMA;
            }
            //去掉最后的,
            return errReasonString.substring(0,errReasonString.length()-1);
        }
        return errReasonString;


    }

    /**
     * 将所有可能的错误原因封装进list
     *
     */
    private List<String> errReasonList(StoreQo storeQo){

        List<String> list = new ArrayList<>();
        boolean storeNoFlag = containSameInfo(COLUMN_NAME_STORE_NO, storeQo.getStoreNo());
        boolean accNoFlag = containSameInfo(COLUMN_NAME_ACC_NO, storeQo.getAccNo());
        boolean cusidFlag = containSameInfo(COLUMN_NAME_CUSID, storeQo.getCusid());
        boolean appidFlag = containSameInfo(COLUMN_NAME_APPID, storeQo.getAppid());
        boolean String50Flag = determineStringLength(storeQo.getStoreName(), LENGTH_50);
        boolean String20Flag = determineStringLength(storeQo.getUserName(), LENGTH_20);
        boolean phoneFlag = orPhoneNumber(storeQo.getPhone());

        if(!storeNoFlag){
            list.add(QUERY_FAIL_CONTAIN_SAME_STORE_NO);
        }
        if(!accNoFlag){
            list.add(QUERY_FAIL_CONTAIN_SAME_ACC_NO);
        }
        if(!cusidFlag){
            list.add(QUERY_FAIL_CONTAIN_SAME_CUSID);
        }
        if(!appidFlag){
            list.add(QUERY_FAIL_CONTAIN_SAME_APPID);
        }
        if(!String50Flag){
            list.add(QUERY_FAIL_STORE_NAME_GREATER_THAN_50);
        }
        if(!String20Flag){
            list.add(QUERY_FAIL_USER_NAME_GREATER_THAN_20);
        }
        if(!phoneFlag){
            list.add(QUERY_FAIL_PHONE);
        }
        return list;
    }

    /**
     * true为含没有该数据，false表示有该数据
     *
     */
    private boolean containSameInfo(String columnName, String columnValue){
        QueryWrapper<StoreInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq(columnName, columnValue);
        if(baseMapper.selectCount(queryWrapper) == SUCCESS_TYPE){
            return false;
        }
        return true;
    }

    /**
     * true为小于等于阈值，false表示大于阈值
     *
     */
    private boolean determineStringLength(String target, int length){
        if(target.length() <= length){
            return true;
        }
        return false;
    }

    /**
     * false表示不合法，true表示合法
     *
     */
    private boolean orPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || "".equals(phoneNumber))
            return false;
        String regex = "^1(3|4|5|7|8)\\d{9}$";
        return phoneNumber.matches(regex);
    }

    /**
     * false表示存商户表失败，true表示存商户表成功
     *
     */
    private boolean saveToStoreInfoTable(StoreQo storeQo){
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setStoreNo(storeQo.getStoreNo());
        storeInfo.setAccNo(storeQo.getAccNo());
        storeInfo.setPhone(storeQo.getPhone());
        storeInfo.setUserName(storeQo.getUserName());
        storeInfo.setStoreName(storeQo.getStoreName());
        storeInfo.setCusid(storeQo.getCusid());
        storeInfo.setAppid(storeQo.getAppid());
        storeInfo.setMd5key(storeQo.getMd5key());
        storeInfo.setCreateTime(getCurrentTime());
        int flag = baseMapper.insert(storeInfo);
        if(flag == SUCCESS_TYPE){
            return true;
        }
        return false;
    }

    private void saveToUserTable(StoreQo storeQo){
        scStoreMapper.saveToUserTable(storeQo);
    }
}

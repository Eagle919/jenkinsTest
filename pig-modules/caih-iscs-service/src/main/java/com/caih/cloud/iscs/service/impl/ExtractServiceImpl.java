package com.caih.cloud.iscs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.common.*;
import com.caih.cloud.iscs.feign.DictFeignService;
import com.caih.cloud.iscs.feign.UserFeignService;
import com.caih.cloud.iscs.mapper.AccMapper;
import com.caih.cloud.iscs.mapper.ExtractMapper;
import com.caih.cloud.iscs.mapper.ScTurnoverMapper;
import com.caih.cloud.iscs.mapper.StoreMapper;
import com.caih.cloud.iscs.model.dto.ExtractDto;
import com.caih.cloud.iscs.model.entity.Acc;
import com.caih.cloud.iscs.model.entity.Extract;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.entity.TurnoverInfo;
import com.caih.cloud.iscs.model.vo.ExtractVo;
import com.caih.cloud.iscs.service.ExtractService;
import com.github.pig.common.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.caih.cloud.iscs.common.Constants.LOCK_PREFIX;

@Service
public class ExtractServiceImpl extends ServiceImpl<ExtractMapper, Extract> implements ExtractService {

    private ExtractMapper extractMapper;
    private AccMapper accMapper;
    private StoreMapper storeMapper;
    private ScTurnoverMapper turnoverMapper;
    private DistributeLock distributeLock;
    private UserFeignService userFeignService;
    private DictFeignService dictFeignService;

    @Autowired
    public ExtractServiceImpl(ExtractMapper extractMapper, AccMapper accMapper,
                              StoreMapper storeMapper, ScTurnoverMapper turnoverMapper,
                              DistributeLock distributeLock, UserFeignService userFeignService,
                              DictFeignService dictFeignService) {
        this.extractMapper = extractMapper;
        this.accMapper = accMapper;
        this.storeMapper = storeMapper;
        this.turnoverMapper = turnoverMapper;
        this.distributeLock = distributeLock;
        this.userFeignService = userFeignService;
        this.dictFeignService = dictFeignService;

    }

    @Override
    public String balance(Integer userId) {
        UserVO userVO = userFeignService.user(userId);
        String accNo = userVO.getUsername();
        QueryWrapper<StoreInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acc_no", accNo);
        StoreInfo storeInfo = storeMapper.selectOne(queryWrapper);
        if (storeInfo == null) {
            return null;
        } else {
            BigDecimal accBalance = storeInfo.getAccBalance();
            DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
            String dfBalance = "0.00";
            if (accBalance != null) {
                dfBalance = decimalFormat.format(accBalance);//会返回格式化后的金额
            }
            return dfBalance;
        }
    }

    @Override
    public Page<ExtractVo> list(ExtractDto dto) {
        Page<ExtractVo> result = new Page<>(dto.getPage(), dto.getLimit());
        Page<Extract> page = new Page<>(dto.getPage(), dto.getLimit());
        List<ExtractVo> extractVos = new ArrayList<>();
        QueryWrapper<Extract> extractQueryWrapper = new QueryWrapper<>();
        //判断是商户还是平台
        UserVO user = userFeignService.user(dto.getUserId());
        if (user == null) return null;
        Integer type = user.getDeptId();
        //按时间倒序
        switch (type) {
            case Constants.ADMIN_TYPE: //admin | pig 超管
                return getExtractVoPage4Manager(dto, result, page, extractVos, extractQueryWrapper);
            case Constants.PLATFORM_TYPE: //manager 平台管理员
                return getExtractVoPage4Manager(dto, result, page, extractVos, extractQueryWrapper);
            case Constants.STORE_TYPE://商户管理员
                String accNo = user.getUsername();
                extractQueryWrapper.eq("acc_no", accNo);
                extractQueryWrapper.orderByDesc("app_time");
                IPage<Extract> extracts4store = extractMapper.selectPage(page, extractQueryWrapper);
                List<Extract> accExtractInfos4store = extracts4store.getRecords();
                packagingExtractVo(extractVos, accExtractInfos4store);
                packagingResult(result, extractVos, extracts4store);
                return result;
            default:
                return null;
        }
    }

    private Page<ExtractVo> getExtractVoPage4Manager(ExtractDto dto, Page<ExtractVo> result, Page<Extract> page, List<ExtractVo> extractVos, QueryWrapper<Extract> extractQueryWrapper) {
        if (extractCondition(dto, extractQueryWrapper)) {
            extractQueryWrapper.orderByDesc("app_time");
            IPage<Extract> extracts4platform = extractMapper.selectPage(page, extractQueryWrapper);
            List<Extract> accExtractInfos4platform = extracts4platform.getRecords();
            packagingExtractVo(extractVos, accExtractInfos4platform);
            packagingResult(result, extractVos, extracts4platform);
            return result;
        } else {
            return result;
        }
    }

    private boolean extractCondition(ExtractDto dto, QueryWrapper<Extract> extractQueryWrapper) {
        boolean result = true;
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (!dto.getStartTime().equals("") && !dto.getEndTime().equals("")) {
                extractQueryWrapper.between("app_time", dto.getStartTime() + " 00:00:00", dto.getEndTime() + " 23:59:59");
            }
        }
        if (dto.getOpener() != null && !"".equals(dto.getOpener())) {
            QueryWrapper<Acc> accQueryWrapper = new QueryWrapper<>();
            accQueryWrapper.like("opener", dto.getOpener());
            List<Acc> accs = accMapper.selectList(accQueryWrapper);
            if (accs.size() <= 0) {
                result = false;
            } else {
                extractQueryWrapper.in("store_no", accs.stream().map(Acc::getStoreNo).collect(Collectors.toSet()));
            }
        }
        if (dto.getExtractState() != null) {
            if (dto.getExtractState() == 0) {//全部
            } else if (dto.getExtractState() == 1) {//未转账 = 待审核 + 已拒绝 + 已通过未转账
                extractQueryWrapper.in("extract_state", 0, 1, 2);
            } else if (dto.getExtractState() == 3) {//已转账
                extractQueryWrapper.in("extract_state", 3);
            } else {
                extractQueryWrapper.eq("extract_state", dto.getExtractState());
            }
        }
        return result;
    }

    private void packagingResult(Page<ExtractVo> result, List<ExtractVo> extractVos, IPage<Extract> extracts) {
        result.setTotal(extracts.getTotal());
        result.setSize(extracts.getSize());
        result.setCurrent(extracts.getCurrent());
        result.setPages(extracts.getPages());
        result.setRecords(extractVos);
    }

    private void packagingExtractVo(List<ExtractVo> extractVos, List<Extract> accExtractInfos) {
        for (Extract extract : accExtractInfos) {
            ExtractVo vo = new ExtractVo();
            vo.setId(extract.getId());
            vo.setExtractTime(extract.getAppTime());
            vo.setDealNo(extract.getExtractNo());
            vo.setExtractAmt(DecimalFormatUtils.formatBigDecimal(extract.getExtractAmt(), null));
            vo.setExtractNo(extract.getExtractNo());
            vo.setStoreNo(extract.getStoreNo());
            vo.setAccNo(extract.getAccNo());
            packagingAccInfo(extract, vo);
            vo.setExtractState(extract.getExtractState());
            vo.setExtractStateName(dictFeignService.label(extract.getExtractState(), Constants.EXTRACT_STATE));
            vo.setRemarks(extract.getRemarks() == null ? "" : extract.getRemarks());
            extractVos.add(vo);
        }
    }

    private void packagingAccInfo(Extract extract, ExtractVo vo) {
        QueryWrapper<Acc> accQueryWrapper;
        accQueryWrapper = new QueryWrapper<>();
        accQueryWrapper.eq("acc_no", extract.getAccNo());
        Acc acc = accMapper.selectOne(accQueryWrapper);
        vo.setExtractAcc(acc.getExtractAcc());
        vo.setBank(acc.getBank());
        vo.setSubBranch(acc.getSubBranch());
        vo.setOpener(acc.getOpener());
    }

    @Override
    public Acc applyExtractInfo(Integer userId) {
        UserVO userVO = userFeignService.user(userId);
        String accNo = userVO.getUsername();
        QueryWrapper<Acc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acc_no", accNo);
        return accMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<Integer, Object> applyExtract(ExtractDto dto) {
        Map<Integer, Object> map = new HashMap<>();
        UserVO userVO = userFeignService.user(dto.getUserId());
        String accNo = userVO.getUsername();
        QueryWrapper<StoreInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("acc_no", accNo);
        StoreInfo store = storeMapper.selectOne(queryWrapper);
        if (StringUtils.isEmpty(store)) {
            map.put(1, false);
            map.put(2, "该账户无可提现金额");
            return map;
        }
        BigDecimal balance = store.getAccBalance();
        if (balance.compareTo(dto.getExtractAmt()) < 0 || balance.compareTo(BigDecimal.ZERO) < 0) {
            map.put(1, false);
            map.put(2, "账号可提现余额不足");
            return map;
        } else {
            String lockKey = LOCK_PREFIX + accNo;
            long threadId = Thread.currentThread().getId();
            String lockFlag = distributeLock.lock(lockKey, Long.toString(threadId), 60000);
            if ("OK".equals(lockFlag)) {//加锁成功
                //更新商户的账户余额
                BigDecimal accBalance = store.getAccBalance().subtract(dto.getExtractAmt());
                store.setAccBalance(accBalance);
                store.setUpdateTime(new Date());
                Extract extract = new Extract();
                extract.setExtractNo(DateUtils.getDateNo());
                extract.setStoreNo(store.getStoreNo());
                extract.setAccNo(accNo);
                extract.setExtractAmt(dto.getExtractAmt());
                extract.setExtractState(0);
                extract.setAppTime(new Date());
                extractMapper.insert(extract);
                storeMapper.updateById(store);
                map.put(1, true);
                map.put(2, "提现申请成功");
                //解锁
                distributeLock.unlock(lockKey, Long.toString(threadId));
                return map;
            } else {
                map.put(1, false);
                map.put(2, "该商户正在加锁，无法操作余额");
                return map;
            }
        }
    }

    @Override
    public Map<Integer, Object> reviewExtract(ExtractDto dto) {
        Map<Integer, Object> map = new HashMap<>();
        String extractNo = dto.getExtractNo();
        //根据提现编号获取提现记录
        QueryWrapper<Extract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("extract_no", extractNo);
        Extract extract = extractMapper.selectOne(queryWrapper);
        String accNo = extract.getAccNo();
        //根据商户账号获取商户信息
        QueryWrapper<StoreInfo> storeInfoQueryWrapper = new QueryWrapper<>();
        storeInfoQueryWrapper.eq("acc_no", accNo);
        StoreInfo storeInfo = storeMapper.selectOne(storeInfoQueryWrapper);
        //根据前端传来的状态进行相关判断
        Integer status = dto.getExtractState();
        String lockKey = LOCK_PREFIX + accNo;
        long threadId = Thread.currentThread().getId();
        String lockFlag = distributeLock.lock(lockKey, Long.toString(threadId), 60000);
        switch (status) {
            case 1:
                if (extract.getExtractState() != 0) {
                    map.put(1, false);
                    map.put(2, "操作失败");
                    break;
                }
                if ("OK".equals(lockFlag)) {//加锁成功
                    extract.setExtractState(1); //拒绝
                    extract.setReviewTime(new Date());
                    extract.setArriveTime(new Date());
                    extract.setTransferTime(new Date());
                    extract.setRemarks(dto.getReason());
                    storeInfo.setAccBalance(extract.getExtractAmt().add(storeInfo.getAccBalance()));
                    storeInfo.setUpdateTime(new Date());
                    extractMapper.updateById(extract);
                    storeMapper.updateById(storeInfo);
                    map.put(1, true);
                    map.put(2, "操作成功");
                    //解锁
                    distributeLock.unlock(lockKey, Long.toString(threadId));
                    break;
                } else {
                    map.put(1, false);
                    map.put(2, "该商户正在加锁，无法操作余额");
                    break;
                }
            case 2:
                if (extract.getExtractState() != 0) {
                    map.put(1, false);
                    map.put(2, "操作失败");
                    break;
                }
                extract.setExtractState(2);//已通过未转账
                extract.setReviewTime(new Date());
                extract.setArriveTime(new Date());
                extract.setTransferTime(new Date());
                extractMapper.updateById(extract);
                map.put(1, true);
                map.put(2, "操作成功");
                break;
            case 3:
                if (extract.getExtractState() != 2) {
                    map.put(1, false);
                    map.put(2, "操作失败");
                    break;
                }
                extract.setExtractState(3); //已转账
                extract.setReviewTime(new Date());
                extract.setArriveTime(new Date());
                extract.setTransferTime(new Date());
                TurnoverInfo turnoverInfo = new TurnoverInfo();
                turnoverInfo.setAccBalance(storeInfo.getAccBalance());
                turnoverInfo.setDealNo(extractNo);
                turnoverInfo.setTurnoverAmt(extract.getExtractAmt());
                turnoverInfo.setTurnoverType(0); //支出
                turnoverInfo.setRemarks("提现到对公账户");
                turnoverInfo.setStoreNo(extract.getStoreNo());
                turnoverInfo.setTurnoverTime(new Date());
                extractMapper.updateById(extract);
                turnoverMapper.insert(turnoverInfo);
                map.put(1, true);
                map.put(2, "操作成功");
                break;
            default:
                map.put(1, false);
                map.put(2, "系统异常,请联系管理员");
                break;
        }
        return map;
    }

    @Override
    public Pair<Boolean, String> getBalance(Integer userId) {
        StoreInfo storeInfo = storeMapper.selectOne(new QueryWrapper<StoreInfo>().eq("acc_no", userFeignService.user(userId).getUsername()));
        if (storeInfo == null) return new Pair<>(false, "0.00");
        if (storeInfo.getAccBalance() == null) return new Pair<>(false, "0.00");
        return new Pair<>(true, new DecimalFormat("###,##0.00").format(storeInfo.getAccBalance()));
    }
}

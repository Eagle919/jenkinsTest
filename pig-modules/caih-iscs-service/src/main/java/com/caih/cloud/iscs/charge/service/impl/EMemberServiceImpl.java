package com.caih.cloud.iscs.charge.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caih.cloud.iscs.charge.mapper.EMemberMapper;
import com.caih.cloud.iscs.charge.model.dto.EMemberDto;
import com.caih.cloud.iscs.charge.model.entity.EBuilding;
import com.caih.cloud.iscs.charge.model.entity.ECommunity;
import com.caih.cloud.iscs.charge.model.entity.EMember;
import com.caih.cloud.iscs.charge.model.vo.ECommunityVo;
import com.caih.cloud.iscs.charge.model.vo.EMemberVo;
import com.caih.cloud.iscs.charge.service.EBuildingService;
import com.caih.cloud.iscs.charge.service.ECommunityService;
import com.caih.cloud.iscs.charge.service.EMemberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class EMemberServiceImpl extends ServiceImpl<EMemberMapper, EMember> implements EMemberService {

    @Resource
    private EMemberMapper memberMapper;

    @Resource
    private ECommunityService communityService;

    @Resource
    private EBuildingService buildingService;

    @Override
    public Page<EMemberVo> members(EMemberDto dto) {
        Page<EMemberVo> page = new Page<>(dto.getPage(), dto.getLimit());
        List<EMember> members = memberMapper.members(page, dto);
        List<EMemberVo> results = new ArrayList<>();
        String memberNo = "";
        for (EMember member : members) {
            if (memberNo.equals(member.getMemberNo())) {
                continue;
            } else {
                memberNo = member.getMemberNo();
            }
            EMemberVo vo = new EMemberVo();
            vo.setId(member.getId());
            vo.setMemberNo(member.getMemberNo());
            vo.setName(member.getName());
            QueryWrapper<ECommunity> cq = new QueryWrapper<>();
            cq.eq("user_no", member.getMemberNo());
            List<ECommunity> communities = communityService.list(cq);
            ArrayList<ECommunityVo> ECommunityVos = new ArrayList<>();
            for (ECommunity community : communities) {
                ECommunityVo communityVo = new ECommunityVo();
                communityVo.setCommunityId(community.getId());
                communityVo.setName(community.getName());
                QueryWrapper<EBuilding> bq = new QueryWrapper<>();
                bq.eq("building_no", community.getBuildingNo());
                List<EBuilding> buildings = buildingService.list(bq);
                communityVo.setBuildings(buildings);
                ECommunityVos.add(communityVo);
            }
            vo.setCommunities(ECommunityVos);
            results.add(vo);

        }
        page.setTotal(results.size());
        return page.setRecords(results);
    }
}

package com.caih.cloud.iscs.charge.model.vo;

import com.caih.cloud.iscs.charge.model.entity.EBuilding;
import lombok.Data;

import java.util.List;

@Data
public class ECommunityVo {
    private Integer communityId;
    private String name;
    private String address;
    private String rate;
    private String remark;
    private Integer countNumber;
    private Integer onlineCount;
    private List<EBuilding> buildings;
}

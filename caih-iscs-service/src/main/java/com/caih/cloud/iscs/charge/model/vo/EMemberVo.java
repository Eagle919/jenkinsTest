package com.caih.cloud.iscs.charge.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class EMemberVo {
    private Integer id;
    private String name;
    private String memberNo;
    private List<ECommunityVo> communities;

}

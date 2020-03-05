package com.caih.cloud.iscs.charge.model.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Geographic {
    private String type;
    private List<Tags> tags;
}

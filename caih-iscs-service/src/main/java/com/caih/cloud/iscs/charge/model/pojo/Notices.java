package com.caih.cloud.iscs.charge.model.pojo;

import lombok.Data;

@Data
public class Notices {
    private String id;
    private String avatar;
    private String title;
    private String datetime;
    private String type;
    private Boolean read;
    private String description;
    private Boolean clickClose;
    private String extra;
    private String status;
}

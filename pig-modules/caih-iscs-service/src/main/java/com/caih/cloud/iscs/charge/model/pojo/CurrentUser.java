package com.caih.cloud.iscs.charge.model.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CurrentUser {
    private String name;
    private String avatar;
    private String userid;
    private String email;
    private String signature;
    private String title;
    private List<Tags> tags;
    private String group;
    private Integer notifyCount;
    private Integer unreadCount;
    private String country;
    private List<List<Tags>> geographic;
    private String address;
    private String phone;
}

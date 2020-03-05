package com.github.pig.admin.model.dto;

import lombok.Data;

import java.util.List;

/**
 * zm.wei
 * 2019-02-22 17:13
 */
@Data
public class RoleMenus {
    private Integer userId;
    private String userName;
    private List<Menu> menus;
}

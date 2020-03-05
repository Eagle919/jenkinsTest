package com.github.pig.admin.model.dto;

import lombok.Data;

/**
 * zm.wei
 * 2019-02-22 17:14
 */
@Data
public class Menu {
    private Integer menuId;
    private Integer parentId;
    private String resourceDesc;
    private String resourceName;
    private String resourceString;
    private String resourceType;
    private String resourceTypeName;
}

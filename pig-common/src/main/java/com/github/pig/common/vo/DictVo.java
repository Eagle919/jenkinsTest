package com.github.pig.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author lengleng
 * @since 2017-11-19
 */
@Data
public class DictVo implements Serializable {

    /**
     * 编号
     */
	private Integer id;
    /**
     * 数据值
     */
	private Integer value;
    /**
     * 标签名
     */
	private String label;
    /**
     * 类型
     */
	private String type;
    /**
     * 描述
     */
	private String description;
    /**
     * 排序（升序）
     */
	private BigDecimal sort;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * 更新时间
     */
	private Date updateTime;
    /**
     * 备注信息
     */
	private String remarks;
    /**
     * 删除标记
     */
	private String delFlag;

}

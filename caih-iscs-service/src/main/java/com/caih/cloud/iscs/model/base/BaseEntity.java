package com.caih.cloud.iscs.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pig.common.util.serializer.DateFormatYmtHmsSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hiro
 * @version v1.0.0
 * @description BaseEntity
 * @date 2018/10/12 11:45
 **/
@Data
public class BaseEntity implements Serializable {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 删除：0 不删除 1 逻辑删除 2 物理删除
     */
    private Integer del = 0;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    /**
     * 修改时间
     */
    @JsonSerialize(using = DateFormatYmtHmsSerializer.class)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime; ///

}

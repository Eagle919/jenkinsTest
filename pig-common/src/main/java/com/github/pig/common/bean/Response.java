package com.github.pig.common.bean;

import com.github.pig.common.bean.constants.Constant;
import lombok.Data;

/**
 * @className Response
 * @Author chenkang
 * @Date 2018/12/3 17:02
 * @Version 1.0
 */
@Data
public class Response<T> {

    /**
     * 返回码
     */
    private Integer code = Constant.RespCode.SUCCESS.getCode();


    private String msg = Constant.RespCode.SUCCESS.getDesc();


    private T data;


}

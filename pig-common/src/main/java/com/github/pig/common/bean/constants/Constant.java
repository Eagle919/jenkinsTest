package com.github.pig.common.bean.constants;

/**
 * @className Constant
 * @Author chenkang
 * @Date 2018/12/3 17:15
 * @Version 1.0
 */
public class Constant {

    public enum RespCode{

        FAIL(0,"请求失败"),
        SUCCESS(1,"请求成功");

        private int code;

        private String desc;

        RespCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}

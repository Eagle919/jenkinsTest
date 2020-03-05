package com.github.pig.common.constant;

/**
 * @className EnumConstants
 * @Author chenkang
 * @Date 2018/11/19 10:42
 * @Version 1.0
 */
public class EnumConstants {

    public enum Reg{

        /**
         * 纯数字
         */
        PURE_NUM("^[0-9]*$"),

        /**
         * 汉字
         */
        CHS_CHAR("^[\\u4e00-\\u9fa5]{0,}$"),


        /**
         * 英文和数字
         */
        EN_NUM("^[A-Za-z0-9]+$"),


        /**
         * 纯英文
         */
        PURE_EN("^[A-Za-z]+$"),

        /**
         * 中文英文数字和下划线
         */
        CHS_EN_NUM_LINE("^[\\u4E00-\\u9FA5A-Za-z0-9_]+$"),

        /**
         * 中文英文数字不包括下划线
         */
        CHS_EN_NUM("^[\\u4E00-\\u9FA5A-Za-z0-9]+$"),

        /**
         * 邮件地址
         */
        EMAIL_ADDR("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"),

        /**
         * 域名
         */
        REALM_NAME("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?"),

        /**
         * 手机号
         */
        PHONE_NUM("^(1[3|6|9][0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|17[2-8]|18[0|1|2|3|5|6|7|8|9])\\d{8}$"),

        /**
         * 身份证
         */
        ID_NUM("^\\d{15}|\\d{18}$");

        private String regex;


        Reg(String regex) {

            this.regex = regex;
        }

        public String getRegex() {
            return regex;
        }
    }

}

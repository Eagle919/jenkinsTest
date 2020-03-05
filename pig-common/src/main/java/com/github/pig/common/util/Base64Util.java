package com.github.pig.common.util;

import org.apache.commons.codec.binary.Base64;

/**
 * @author yuwei
 * @date 2018/11/27 11:14
 */
public class Base64Util {
    private Base64Util(){}
    //base64字符串转byte[]
    public static byte[] base64String2Byte(String base64Str){
        return Base64.decodeBase64(base64Str);
    }
    //byte[]转base64
    public static String byte2Base64String(byte[] b){
        return Base64.encodeBase64String(b);
    }
}

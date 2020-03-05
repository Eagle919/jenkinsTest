package com.github.pig.common.util;

/**
 * @className NorUtils 一般工具集合
 * @Author chenkang
 * @Date 2019/1/14 10:32
 * @Version 1.0
 */
public class NorUtils {

    private static final char UNDERLINE  = '_';

    private NorUtils() {
    }

    public static String covertToHump(String str){

        //全部转为小写
        return recurseCover(str.toLowerCase());
    }

   private static String recurseCover(String str){
       if(str.indexOf(UNDERLINE) < 0){
           return str;
       }

       int index = str.indexOf(UNDERLINE);

       String prefix = str.substring(0,index);
       //将后一个字母转为大写
       String changed = str.substring(index+1,index+2).toUpperCase();

       String taill = str.substring(index+2);

       String rebuildStr = prefix + changed +taill;

       return recurseCover(rebuildStr);
   }

}

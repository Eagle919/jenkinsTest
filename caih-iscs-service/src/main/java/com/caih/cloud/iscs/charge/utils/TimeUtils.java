package com.caih.cloud.iscs.charge.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * 取得系统时间
     *
     * @param pattern eg:yyyy-MM-dd HH:mm:ss,SSS
     * @return
     */
    public static String getSysTime(String pattern) {

        return formatSysTime(new SimpleDateFormat(pattern));
    }

    /**
     * 格式化系统时间
     *
     * @param format
     * @return
     */
    private static String formatSysTime(SimpleDateFormat format) {

        String str = format.format(Calendar.getInstance().getTime());
        return str;
    }


    public static String timeDiffer(Date endTime, Date startTime) {
        try {
            long diff = endTime.getTime() - startTime.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            if (hours < 0) hours = 0;
//            return hours + "." + minutes; //整数及小数
            return hours + ""; //只取整数
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static Date addDateHour(Date date, int hour) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        return date;

    }

    /**
     * 获取当前时间 yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

    /**
     *      * 判断时间是否在时间段内
     *      * @param nowTime
     *      * @param beginTime
     *      * @param endTime
     *      * @return
     *      
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

}

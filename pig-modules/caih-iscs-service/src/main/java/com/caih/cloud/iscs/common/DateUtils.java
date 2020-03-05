package com.caih.cloud.iscs.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTimeUtils;
import org.joda.time.Interval;
import org.joda.time.Period;

public class DateUtils {

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    /**
     * * 获取现在时间
     * *
     * * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        return formatter.parse(dateString, pos);
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss:SS格式 精确到毫秒
     *
     * @return String类型的提现编号
     */
    public static String getDateNo() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        String format = sdfTime.format(new Date());
        String result = format.replaceAll("[[\\s-:punct:]]", "");
        Random r = new Random();
        int i = r.nextInt(900000) + 100000;
        return result + i;
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss 精确到毫秒
     *
     * @return
     */
    public static String getDateString(String date) {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdfTime.format(date);
    }

    /**
     * 获取随机的流水号(10位)
     *
     * @return
     */
    public static String getExtractNo() {
        int length = 5;
        String base = "QWERTYUIOPLKJHGFDSAZXCVBNM0123456789";
        Random random = new Random();
        SimpleDateFormat sdfTime = new SimpleDateFormat("MM-dd HH:mm:ss");
        String format = sdfTime.format(new Date());
        String result = format.replaceAll("[[\\s-:punct:]]", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString() + result;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return boolean
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 时间差 单位：分钟
     */
    public static int timeDifference(String startTime, String endTime) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long from = 0;
        try {
            from = simpleFormat.parse(startTime).getTime();
            long to = simpleFormat.parse(endTime).getTime();
            return (int) ((to - from) / (1000 * 60));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }


}

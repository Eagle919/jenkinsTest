package com.github.pig.common.util;

import com.google.common.base.Strings;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @className DateUtil
 * @Author chenkang
 * @Date 2018/11/9 17:29
 * @Version 1.0
 */
public final class DateUtil {
    private DateUtil(){}

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String formateDate(Date date, String pattern){
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 默认24小时
     * @param
     * @return
     * @throws ParseException
     */
    public static String formateDate(Date date ){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 默认24小时
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String formateDate(String dateStr ) throws ParseException {
        if (StringUtils.isEmpty(dateStr)){
            return "";
        }
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(dateStr);
        return df.format(date);
    }


    public String getCurrentYear(){
        Calendar calendar = Calendar.getInstance();

        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 默认24小时
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String formateDateNoTime(String dateStr ) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            return "";
        }
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(dateStr);
        return df.format(date);
    }


    /**
     * 预警时间
     * @param day
     * @return
     */
    public static String warningDateUtil(int day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar warmingDate = Calendar.getInstance();
        warmingDate.setTime(new Date());
        warmingDate.add(Calendar.DAY_OF_YEAR, day);
        return sdf.format(warmingDate.getTime());
    }

    /**
     * dateString to yyyy-MM-dd 样式
     *
     */
    public static String dateStrToyMd(String date){
        if (Strings.isNullOrEmpty(date) || date.length() < 10){
            return "";
        }
        return date.substring(0,10);
    }

    /**
     * 获取当前日期，没有横线的
     * @return
     */
    public static String currentDateStr() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DateUtil.YYYYMMDD);
        LocalDate today = LocalDate.now();
        return today.format(dtf);
    }

    /**
     * 获取当前时间，没有横线的
     * @return
     */
    public static String currentDateTimeStr() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DateUtil.YYYYMMDDHHMMSS);
        LocalDateTime today = LocalDateTime.now();
        return today.format(dtf);
    }

    /**
     * 距离下一天的秒数
     * @return
     */
    public static Long distanceToNextDay() {
        Date currentDate = new Date();
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        return ChronoUnit.SECONDS.between(currentDateTime, midnight);
    }
}

package com.caih.cloud.iscs.charge.scoket;

import com.caih.cloud.iscs.charge.scoket.Util.BCDUtil;
import com.caih.cloud.iscs.charge.scoket.Util.HexUtil;
import com.caih.cloud.iscs.charge.scoket.Util.MianCore;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 设备操作助手
 */
public class DeviceHandler {
    private static final String startChar = "FAF5"; // 起始字符
    private static final String versionChar = "0100";// 版本号
    private static final String cRCChar = "9CA9";// CRC编码
    private static final String endChar = "16";// 结束字符
    private static final Logger logger = LoggerFactory.getLogger(DeviceHandler.class);
    private static MianCore mianCore = new MianCore();

    /**
     * 根据设备号 返回设备端口状态
     *
     * @param deviceCode
     * @return
     */
    public static List<DeviceStatus> getDeviceStatusByCode(String deviceCode) {
        List<DeviceStatus> deviceStatusList = MessageHandler.deviceMap.get(deviceCode);
        logger.info("[设备{}状态->{}]",deviceCode,deviceStatusList);
        return deviceStatusList;
    }

    /**
     * 通过设备号，给设备发送充电指令信息
     *
     * @param deviceCode 设备编号 如10002998
     * @param time       充电时长 单位：分钟
     * @param port       充电端口 0～9
     */
    public static void sendStartChargeMessage(String deviceCode, int time, int port) throws Exception {
        String length = "16";//  帧长度
        String chargeHeader = "57";
        String chargeTime = Integer.toHexString(time);
        String deviceCodeHexString = HexUtil.string2HexStringNotSpace(deviceCode);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startChar);
        stringBuilder.append(length);
        stringBuilder.append(versionChar);
        stringBuilder.append("04");// 帧序列号
        stringBuilder.append(deviceCodeHexString);
        stringBuilder.append(chargeHeader);
        stringBuilder.append("02");// 启动模式以及启动源
        stringBuilder.append(chargeTime + "00");// 充电时长数据包
        stringBuilder.append("0" + port);// 端口
        stringBuilder.append(cRCChar);
        stringBuilder.append(endChar);

        String output = HexUtil.formateString(stringBuilder.toString());

        IoSession session = MessageHandler.sessionMap.get(deviceCode);
        if (null == session) {
            throw new Exception("设备暂未连接");
        }
        mianCore.sendMsg(output, session);
        logger.info("[向设备:{}下发充电指令->:{}]", deviceCode, output);
    }

    /**
     * 根据充电桩编号、端口号停止充电
     *
     * @param deviceCode 充电桩编号
     * @param port       端口号
     */
    public static void sendStopChargeMessage(String deviceCode, int port) throws Exception {

        String length = "13";//  帧长度
        String stopChargeHeader = "5B";
        String deviceCodeHexString = HexUtil.string2HexStringNotSpace(deviceCode);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startChar);
        stringBuilder.append(length);
        stringBuilder.append(versionChar);
        stringBuilder.append("00");// 帧序列号
        stringBuilder.append(deviceCodeHexString);
        stringBuilder.append(stopChargeHeader);
        stringBuilder.append("0" + port);// 端口
        stringBuilder.append(cRCChar);
        stringBuilder.append(endChar);

        String output = HexUtil.formateString(stringBuilder.toString());

        IoSession session = MessageHandler.sessionMap.get(deviceCode);
        if (null == session) {
            throw new Exception("设备暂未连接");
        }
        mianCore.sendMsg(output, session);
        logger.info("[向设备:{}下发停止充电指令->:{}]", deviceCode, output);
    }


    /**
     * 给签到的设备同步时间 年月日时分秒
     *
     * @param deviceCode
     */
    public static void timeSynchronization(String deviceCode) throws Exception {
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR)).substring(2, 4);
        String month = String.valueOf(now.get(Calendar.MONTH) + 1);
        String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(now.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(now.get(Calendar.MINUTE));
        String srcond = String.valueOf(now.get(Calendar.SECOND));

        if (minute.length() == 1) {
            minute += "0" + minute;
        }
        if (srcond.length() == 1) {
            srcond += "0" + srcond;
        }

        String dateStr = year + month + day + hour + minute + srcond;

        String length = "18";//  帧长度
        String stopChargeHeader = "50";
        String deviceCodeHexString = HexUtil.string2HexStringNotSpace(deviceCode);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startChar);
        stringBuilder.append(length);
        stringBuilder.append(versionChar);
        stringBuilder.append("00");// 帧序列号
        stringBuilder.append(deviceCodeHexString);
        stringBuilder.append(stopChargeHeader);
        stringBuilder.append(dateStr);// 同步时间
        stringBuilder.append(cRCChar);
        stringBuilder.append(endChar);

        String output = HexUtil.formateString(stringBuilder.toString());


        IoSession session = MessageHandler.sessionMap.get(deviceCode);
        if (null == session) {
            throw new Exception("设备暂未连接");
        }
        mianCore.sendMsg(output, session);
        logger.info("[向设备:{}下发同步时间指令->:{}]", deviceCode, output);
    }

    public static void main(String[] args) throws Exception {
//        sendStartChargeMessage("10002998", 60, 0);
//        sendStopChargeMessage("10002998", 0);
//        System.out.println("2019".substring(2, 4));
        timeSynchronization("10002998");
    }


}

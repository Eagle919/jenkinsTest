package com.caih.cloud.iscs.charge.scoket;

import com.caih.cloud.iscs.charge.scoket.Util.HexUtil;
import com.caih.cloud.iscs.charge.scoket.Util.SessionUtil;
import org.apache.mina.core.session.IoSession;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;


public class MessageHandler {


    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    public static List<DeviceStatus> devicePortStatus = new ArrayList<>();// 设备状态数组，每30秒刷新一次

    public static Map<String, List<DeviceStatus>> deviceMap = new HashMap<>(); // 设备端口
    public static Map<String, IoSession> sessionMap = new HashMap<>();
    private static String startChar = "FAF5"; // 起始字符
    private static String versionChar = "0100";// 版本号
    private static String cRCChar = "9CA9";// CRC编码
    private static String endChar = "16";// 结束字符

    /**
     * 设备签到回复
     *
     * @param deviceCode  设备编号 16进制
     * @param runningChar 报文流水 16进制
     * @return
     */
    public static String deviceSign(IoSession session, String deviceCode, String runningChar) throws UnsupportedEncodingException {
        String signCode = "11";// 签到相应码
        String signSuccessCode = "01";// 成功相应码
        String signFailuerCode = "00";// 失败相应码
        String length = "13";//  帧长度
        String nextRunningChar = nextRunningChar(runningChar);
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append(startChar);
        stringBuilder.append(length);
        stringBuilder.append(versionChar);
        stringBuilder.append(nextRunningChar);
        stringBuilder.append(deviceCode);
        stringBuilder.append(signCode);
        stringBuilder.append(signSuccessCode);
        stringBuilder.append(cRCChar);
        stringBuilder.append(endChar);

        String responeString = HexUtil.formateString(stringBuilder.toString());
        logger.info("[设备:{}签到,签到回复码->:{}]", HexUtil.HexStringToString(deviceCode), responeString);
        sessionMap.put(HexUtil.HexStringToString(deviceCode), session);
        deviceMap.put(HexUtil.HexStringToString(deviceCode), devicePortStatus);

        return responeString;
    }

    /**
     * 设备心跳回复
     *
     * @param deviceCode  设备编号 16进制
     * @param runningChar 报文流水 16进制
     * @param beaNum      心跳序号 16进制
     * @return
     */
    public static String beat(String deviceCode, String runningChar, String beaNum) throws UnsupportedEncodingException {
        String length = "14";// 帧长度
        String beatCode = "13";// 心跳相应码
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append(startChar);// 开始
        stringBuilder.append(length);// 帧长度
        stringBuilder.append(versionChar);// 版本号
        stringBuilder.append(nextRunningChar(runningChar));//流水号 自定增加
        stringBuilder.append(deviceCode);
        stringBuilder.append(beatCode);
        stringBuilder.append(nextBeatNum(beaNum));// 下一个心跳序号
        stringBuilder.append(cRCChar);
        stringBuilder.append(endChar);

        String responeString = HexUtil.formateString(stringBuilder.toString());
        logger.info("[设备:{}心跳响应回复码->:{}]", HexUtil.HexStringToString(deviceCode), responeString);

        return responeString;
    }

    /**
     * @return
     */
    public static String stopCharge() {
        return "停止充电";
    }


    public static List<String> communicate(IoSession session, String val) throws UnsupportedEncodingException {
        List<String> resList = new ArrayList<>();
        String reString = "";
        String[] vals = StringUtils.delimitedListToStringArray(val, " ");
        String deviceCode = vals[6] + vals[7] + vals[8] + vals[9] + vals[10] + vals[11] + vals[12] + vals[13];
        String runningChar = vals[5];
        if (vals == null || vals.length == 0) {
            resList.add(val);
            return resList;
        }

        switch (vals[14]) {
            case "10": // 签到响应
                reString = deviceSign(session, deviceCode, runningChar);
                break;
            case "12": // 心跳响应
                reString = beat(deviceCode, runningChar, vals[15] + vals[16]);
                deviceStatus(vals[17], vals[18], deviceCode);
                break;
            case "58": // 充电响应
//                reString = beat(hexStrig, runningChar, vals[15] + vals[16]);
                break;
            case "5C": // 停止充电响应
//                reString = beat(hexStrig, runningChar, vals[15] + vals[16]);
            default:
                break;
        }

        resList.add(reString);
        resList.add(deviceCode);

        return resList;
    }

    /**
     * @param runningChar 16进制的编码 最长FF 即255
     * @return 下一个流水号
     */
    public static String nextRunningChar(String runningChar) throws UnsupportedEncodingException {

        // 如果runningChar =255 则从0开始计数
        if ("FF".equals(runningChar)) {
            return "00";
        }

        // 将runningChar 转化成10进制
        int hexToString = Integer.parseInt(runningChar, 16);

        // 10进制+1后，格式话成16进制 返回
        int hex = hexToString + 1;
        String hexString = Integer.toHexString(hex);


        if (hexString.length() < 2) {
            hexString = "0" + hexString;
        }

        logger.info("[hexString->{}]", hexString);

        return hexString;
    }

    /**
     * 心跳序号 传入16进制的字符串；最小0000，最大ffff
     *
     * @param beaNum
     * @return 计算出下一个心跳序号并返回
     * @throws UnsupportedEncodingException
     */
    public static String nextBeatNum(String beaNum) throws UnsupportedEncodingException {
        String nextHexBeatNum;

        String firstHex;
        String lastHex;

        String firstNum = beaNum.substring(0, 2);
        String lastNum = beaNum.substring(2, 4);

        if ("ff".equals(lastNum) || "FF".equals(lastNum)) {
            lastHex = "00";
            firstHex = nextRunningChar(firstNum);

            return firstHex + lastHex;
        }

        if ("ff".equals(firstNum) || "FF".equals(firstNum)) {
            firstHex = "00";
            lastHex = nextRunningChar(lastNum);

            return firstHex + lastHex;
        }

        lastHex = nextRunningChar(lastNum);
        nextHexBeatNum = firstNum + lastHex;

        return nextHexBeatNum;
    }

    /**
     * 解析设备状态 和 GPRS信号强度
     *
     * @param hexVal1 16进制转2进制 然后拆分数组返回
     * @return
     */
    public static void deviceStatus(String hexVal1, String hexVal2, String deviceCode) {
        devicePortStatus.clear();

        // 将16进制转化成2进制字符串 后转为数组
        byte[] bs1 = HexUtil.hexStringToBytes(hexVal1);
        String binString1 = HexUtil.byteArrToBinStr(bs1);
        String[] binStrArray1 = StringUtils.delimitedListToStringArray(new StringBuilder(binString1).reverse().toString(), "");

        for (int i = 0; i < binStrArray1.length; i++) {
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setPort(i);
            deviceStatus.setStatus(binStrArray1[i]);
            devicePortStatus.add(deviceStatus);

        }

        // 将16进制转化成2进制字符串 后转为数组
        byte[] bs2 = HexUtil.hexStringToBytes(hexVal2);
        String binString2 = HexUtil.byteArrToBinStr(bs2);
        String[] binStrArray2 = StringUtils.delimitedListToStringArray(new StringBuilder(binString2).reverse().toString(), "");

        for (int i = 0; i < 2; i++) {
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setPort(devicePortStatus.size());
            deviceStatus.setStatus(binStrArray2[i]);
            devicePortStatus.add(deviceStatus);
        }
        try {
            deviceMap.put(HexUtil.HexStringToString(deviceCode), devicePortStatus);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        FF 83  1718
//        String el = "FA F5 16 01 00 02 31 30 30 30 32 39 39 38 12 00 02 FF 9B FF FF 16";
//        String el2 = "FA F5 16 01 00 AE 31 30 30 30 32 39 39 38 12 00 AE FF 83 FF FF 16";
//        String reString = communicate(el);
//
//        String nextBeatNum = nextBeatNum("fffe");
//
//        String test = HexUtil.HexStringToString("3130303032393938");
//          deviceStatus("FF","83");

//         String el= HexUtil.string2HexString("10002998");
        String result = Integer.toBinaryString(9);
//        00001001
        System.out.println(result);
    }
}

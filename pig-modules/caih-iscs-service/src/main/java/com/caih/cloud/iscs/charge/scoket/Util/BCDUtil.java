package com.caih.cloud.iscs.charge.scoket.Util;

/**
 * BCD-8421码工具
 */
public class BCDUtil {

	/**
	 * byte[] -> 十进制字符串
	 * 
	 * @param b
	 *            byte数组（不能为空）
	 * @return 十进制字符串
	 * @throws NullPointerException
	 *             数组为空
	 */
	public static String toStr(byte[] b) {
		if (b == null || b.length < 1) {
			throw new NullPointerException("数组不能为空！");
		}

		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append((byte) ((b[i] & 0xF0) >>> 4));
			sb.append((byte) (b[i] & 0x0F));
		}
		return sb.toString();
	}

	/**
	 * 十进制字符串 -> byte[]
	 * 
	 * @param decimal
	 *            十进制字符串（不能为空）
	 * @return byte数组
	 * @throws NullPointerException
	 *             十进制字符串为空
	 */
	public static byte[] toByte(String decimal) {
		if (decimal == null || "".equals(decimal = decimal.trim())) {
			throw new NullPointerException("十进制字符串不能为空！");
		}
		if (decimal.length() % 2 != 0) {// 长度为奇数
			decimal = "0" + decimal;
		}

		byte[] b = decimal.getBytes();
		byte[] result = new byte[b.length / 2];
		int m, n;
		for (int i = 0; i < result.length; i++) {
			m = b[2 * i];
			if (m >= '0' && m <= '9') {
				m -= '0';
			} else if (m >= 'a' && m <= 'z') {
				m -= 'a' + 0x0A;
			} else {
				m -= 'A' + 0x0A;
			}
			n = b[2 * i + 1];
			if (n >= '0' && n <= '9') {
				n -= '0';
			} else if (n >= 'a' && n <= 'z') {
				n -= 'a' + 0x0A;
			} else {
				n -= 'A' + 0x0A;
			}
			result[i] = (byte) ((m << 4) + n);
		}
		return result;
	}

}

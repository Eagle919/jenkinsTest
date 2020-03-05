package com.caih.cloud.iscs.charge.scoket.Util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MianCore {
	
	static Logger logger = LoggerFactory.getLogger(MianCore.class);
	
	 //给客户端发送消息
	 public static void sendMsg(String msg,IoSession session){
    	 msg=msg.replace(" ", "");
         byte[] bts=HexUtil.hexStringToBytes(msg);
         IoBuffer buffer = IoBuffer.allocate(bts.length);  
         buffer.put(bts);  
         buffer.flip();  
         session.write(buffer);  
     }
	 
	 //字符串转Unicode
	 public static String string2Unicode(String string) {
		    StringBuffer unicode = new StringBuffer();
		    for (int i = 0; i < string.length(); i++) {
		        char c = string.charAt(i);
		        unicode.append(Integer.toHexString(c)+" ");
		    }
		    return unicode.toString();
	 }
	 
	
	public static String bu0(String str){
		if(str.length() == 1){
			str = "0"+ str;
		}
		return str;
	}
	
	//进制转换10-16
	public static String en10to16(int length){
		String temp="";
		String a=Integer.toHexString(length);
		if(a.length()==1){
			temp="0"+a.toUpperCase();
		}else temp=a;
		return temp;
	}
	
}

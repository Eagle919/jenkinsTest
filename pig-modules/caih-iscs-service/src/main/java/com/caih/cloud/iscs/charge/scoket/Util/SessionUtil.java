package com.caih.cloud.iscs.charge.scoket.Util;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class SessionUtil {
	public static Map<String,IoSession> sessionMap =new HashMap<>();
	
	public void addSession(String key,IoSession session){
		sessionMap.put(key, session);
	}
	
	public IoSession getSession(String key){
		return sessionMap.get(key);
	}

	public void removeSession(String key){
	    sessionMap.remove(key);
    }
	
	public static IoBuffer encode(String str){
        String[] cmds = str.split(" ");
        byte[] arr = new byte[cmds.length];
        int i = 0;
        for (String b : cmds) {
            if (b.equals("FF")) {
                arr[i++] = -1;
            } else {
                arr[i++] = Byte.parseByte(b, 16);
            }
        }
        return IoBuffer.wrap(arr); 
	}
	
	
	
}

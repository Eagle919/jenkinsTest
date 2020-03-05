package com.caih.cloud.iscs.charge.scoket;

import com.caih.cloud.iscs.charge.scoket.Util.HexUtil;
import com.caih.cloud.iscs.charge.scoket.Util.MianCore;
import com.caih.cloud.iscs.charge.scoket.Util.SessionUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServerHandler extends IoHandlerAdapter {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MianCore mianCore =new MianCore();


    private static ServerHandler serverHandler ;


    @PostConstruct
    public void init() {
        serverHandler = this;
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception { //用户连接到服务器
        SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();
        cfg.setSoLinger(0);
        logger.info("[服务建立]" + session.getId());

    }

    @Override
    public void messageReceived(IoSession session, Object message)throws Exception {//接收消息
        IoBuffer bbuf = (IoBuffer) message;
        byte[] byten = new byte[bbuf.limit()];
        bbuf.get(byten, bbuf.position(), bbuf.limit());
        String hexString = HexUtil.bytes2HexString(byten);// bytes转16进制


//        String hexStringTest=HexUtil.HexStringToString(hexString); // 部署时去除
        logger.info("[收到16进制消息->:{}]" , hexString);

        String responeString = MessageHandler.communicate(session,hexString).get(0);

        mianCore.sendMsg(responeString,session);
        String[] resStr= responeString.split(" ");

        if("11".equals(resStr[14])){
            try {
                DeviceHandler.timeSynchronization(HexUtil.HexStringToString(MessageHandler.communicate(session,hexString).get(0)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if
    }



    @Override
    public void sessionClosed(IoSession session) throws Exception {   //设备从服务器断开
        logger.info("[服务断开]" + session.getId());
    }
    @Override
    public void messageSent(IoSession session, Object message){ //发送消息结束
        //logger.info("[发送消息结束]" + session.getId() + "message" + message);
    }


    @Override
    public void sessionIdle(IoSession session, IdleStatus status)throws Exception {//重连
        logger.info("[服务重连]" + session.getId() + "status" + status.toString());
        this.sessionClosed(session);
    }


    @Override
    public void exceptionCaught(IoSession session, Throwable cause)throws Exception {//连接发生异常
        cause.printStackTrace();
        logger.info("[服务Id{}异常:->{}]" + session.getId(),cause.getMessage());

    }

}

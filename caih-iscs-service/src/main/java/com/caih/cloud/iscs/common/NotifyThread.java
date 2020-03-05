package com.caih.cloud.iscs.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NotifyThread implements Runnable{

    @Value("${allinpay.secret}")
    private String secret;

    private int retNum;//执行次数

    private Map<String, String> paramMap;

    private String retUrl;

    public NotifyThread(Map<String, String> paramMap, String retUrl, int retNum){
        this.paramMap = paramMap;
        this.retUrl = retUrl;
        this.retNum = retNum;
    }

    public void run() {
        System.out.println("------------NotifyThread------------");
        System.out.println("------------retNum："+this.retNum+"------------");
        System.out.println("------------retUrl："+this.retUrl+"------------");
        //参数拼接 name1=value1&name2=value2
        StringBuffer param = new StringBuffer("");
        param.append("orderNo=" + paramMap.get("orderNo"));
        param.append("&dealNo=" + paramMap.get("dealNo"));
        param.append("&orderState=" + paramMap.get("orderState"));
        param.append("&trxstatus=" + paramMap.get("trxstatus"));
        //加签
        param.append("&sign=" + AllInPayUtil.signData(paramMap, secret));
        String retMsg = HttpRequestUtil.sendGet(retUrl, param.toString());//第一次
        AtomicInteger atomicInteger = new AtomicInteger(this.retNum);
        while (!"success".equals(retMsg) && atomicInteger.getAndDecrement() > 1) {
            try {
                Thread.sleep(60000);
                System.out.println("------------retNum："+atomicInteger+"------------");
                retMsg = HttpRequestUtil.sendGet(retUrl, param.toString());
            }catch (InterruptedException e){
                log.error(e.getMessage());
            }
        }

        System.out.println("------------NotifyThread success------------");
    }
}

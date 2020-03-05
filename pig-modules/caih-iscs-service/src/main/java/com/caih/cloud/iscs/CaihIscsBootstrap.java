package com.caih.cloud.iscs;

import com.caih.cloud.iscs.charge.scoket.ServerHandler;
import com.spring4all.swagger.EnableSwagger2Doc;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;



/**
 * @author yuwei
 * @date 2018/12/14 10:09
 */
@EnableAsync
@EnableScheduling
@EnableSwagger2Doc
@EnableFeignClients(basePackages = {"com.caih.cloud.iscs", "com.github.pig.common.bean"})
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan({"com.caih.cloud.iscs", "com.github.pig.common.bean"})
@EnableTransactionManagement
public class CaihIscsBootstrap  extends SpringBootServletInitializer {

    private static final int PORT = 4007;

    public static void main(String[] args) {

        SpringApplication.run(CaihIscsBootstrap.class, args);
    }



    @Override
    public SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return  builder.sources(CaihIscsBootstrap.class);
    }

    @Bean
    public IoAcceptor ioAcceptor() throws Exception {
        IoAcceptor acceptor=new NioSocketAcceptor();
        acceptor.setHandler(new ServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(1024);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 100);
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("服务器在端口：" + PORT + "已经启动");
        return acceptor;
    }

}



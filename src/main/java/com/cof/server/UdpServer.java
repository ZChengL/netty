package com.cof.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

/**
 * @author lilongke
 *  *@ClassName UdpServer
 * @since 2020.05.08
 * 监听udp连接的Netty服务端
 */


@Service
public class UdpServer {
    private volatile boolean isFirst = false;
    private static Object object = new Object();
    //服务器地址端口

    @Value("${UdpServer.port}")
    private int PORT = 7777;

    public Logger logger = LogManager.getLogger("【UPDServer.server】"+ UdpServer.class.getName());

    /** 用于分配处理业务线程的线程组个数 */
    protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    /**
     * SpringBoot项目启动后, 自动启动Udp Netty服务端
     */
    @PostConstruct
    public void startupUdpServer() {
        new Thread(this::run).start();
    }

    //    线程内容
    private void run(){
        if (!isFirst) {
            synchronized (this) {
                if (!isFirst) {
                    CompletableFuture.runAsync(() -> {
                        Bootstrap bootstrap = new Bootstrap();
                        bootstrap.group(bossGroup)
                                .channel(NioDatagramChannel.class)
                                .localAddress(PORT)
//                                .option(ChannelOption.SO_BROADCAST, true)
                                .handler(new ChannelInitializer<NioDatagramChannel>() {

                                    @Override
                                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                                        ch.pipeline().addLast(new UdpServerHandler());
                                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                                    }
                                });
                        try {
                            bootstrap.bind(PORT).sync().channel().closeFuture().await();
                        } catch (InterruptedException e) {
                            logger.error("UDP Netty 服务端启动失败!");
                            e.printStackTrace();
                        }
                    });
                    isFirst = true;
                    logger.info("UDP Netty 服务端启动成功，port=["+PORT+"]，等待客户发送数据包...");
                }
            }
        }
    }
}

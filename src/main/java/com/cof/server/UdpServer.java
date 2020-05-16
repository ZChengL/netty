package com.cof.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
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
    //EventExecutorGroup executors = new EventExecutorGroup();
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
                                //由于UDP是无会话的，所以只有一个线程可以在一个UDP端口上接收数据并对其进行解码。
                                //创建NioDatagramChannelFactory时可以指定的池的线程仅当您在多个端口上侦听数据时使用。每个端口只有一个线程有意义。即使您在该构造函数中指定了100个工作程序，也只会使用一个，如果您配置了一个UDP端口。
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

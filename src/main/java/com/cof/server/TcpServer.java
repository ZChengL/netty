package com.cof.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author lilongke
 *  *@ClassName TcpServer
 * @since 2020.05.08
 * 监听tcp连接的Netty服务端
 */


@Service
public class TcpServer {

    // 服务器地址端口
    @Value("${TcpServer.ip}")
    private String IP = "127.0.0.1";

    @Value("${TcpServer.port}")
    private int PORT = 9999;

    public Logger logger = LogManager.getLogger("【TCPServer.server】"+ TcpServer.class.getName());

    /** 用于分配处理业务线程的线程组个数 */
    protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    /** 业务出现线程大小 */
    protected static final int BIZTHREADSIZE = 4;

    /*
     * NioEventLoopGroup实际上就是个线程池,
     * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
     * 每一个NioEventLoop负责处理m个Channel,
     * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
     */
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    /**
     * SpringBoot项目启动后, 自动启动Tcp Netty服务端
     */
    @PostConstruct
    public void startupTcpServer() {
        new Thread(this::run).start();
    }

    //    线程内容
    private void run(){
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        b.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                Decode是对发送的信息进行编码、
//                @param maxFrameLength  帧的最大长度
//                @param lengthFieldOffset length字段偏移的地址
//                @param lengthFieldLength length字段所占的字节
//                @param lengthAdjustment 修改帧数据长度字段中定义的值，
//                可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
//                @param initialBytesToStrip 解析时候跳过多少个长度
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new TcpServerHandler());
            }
        });
//        异步绑定端口
        try{
            b.bind(IP, PORT).sync();
        }catch(Exception e){
            logger.error("Tcp Netty 服务端启动失败!");
        }
        logger.info("Tcp Netty 服务端已启动,IP=["+IP+"] port=["+PORT+"],等待客户端连接...");
    }
//            关闭端口
    protected static void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}

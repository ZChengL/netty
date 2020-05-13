package com.cof.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.annotation.PostConstruct;

/**
 * @author lilongke
 *  *@ClassName WebSocketServer
 * @since 2020.05.08
 * 监听WebSocket连接的Netty服务端
 */

@Service
public class WebSocketServer {

    @Value("${WebSocketServer.ip}")
    private String HOST = "127.0.0.1";

    @Value("${WebSocketServer.port}")
    private int PORT = 7777;

    @Value("${WebSocketServer.sockPath}")
    private String WEB_SOCKET_PATH = "/ws";

    public Logger logger = LogManager.getLogger("【websocket.server】"+WebSocketServer.class.getName());
    /**
     * SpringBoot项目启动后, 自动启动WebSocket Netty服务端
     */
    @PostConstruct
    public void startupWebSocketServer() {
        new Thread(this::run).start();
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                    pipeline.addLast("httpCodec", new HttpServerCodec());
                    //以块的方式来写的处理器
                    pipeline.addLast("httpChunked", new ChunkedWriteHandler());
                    //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
                    pipeline.addLast("aggregator", new HttpObjectAggregator(64 * 1024));
                    pipeline.addLast("webSocketPath", new WebSocketServerProtocolHandler(WEB_SOCKET_PATH));
                    pipeline.addLast("webSocketHandler", new WebSocketHandler());
                }
            });
            ChannelFuture future = bootstrap.bind(HOST, PORT).sync();
            logger.info("WebSocket Netty 服务端已启动,IP=["+HOST+"] port=["+PORT+"],等待客户端连接...");
            future.channel().closeFuture().sync(); //相当于在这里阻塞，直到server channel关闭
        } catch (Exception e) {
            logger.error("WebSocket Netty服务端启动发生异常", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
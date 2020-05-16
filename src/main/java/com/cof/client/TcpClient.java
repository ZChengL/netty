package com.cof.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author lilongke
 * @ClassName TcpClient
 **/
public class TcpClient {

    public static String HOST = "127.0.0.1";
    public static int PORT = 9999;

    public static Bootstrap bootstrap = getBootstrap();
    public static Channel channel = getChannel(HOST, PORT);

    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("handler", new TcpClientHandler());
            }
        });
        //ChannelOption.RCVBUF_ALLOCATOR
        b.option(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }

//    连接端口
    public static final Channel getChannel(String host, int port) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (Exception e) {
            System.out.println("Connect Server(IP{},PORT{}) Failed"+"host:"+host+"port:"+port+"e:"+e);
            return null;
        }
        return channel;
    }

    /**
        * 发送信息
        * @param
        * @return void
        */
    public static void sendMsg(String msg) throws Exception {
        if (channel != null) {
            while(true){
                System.out.println("输入消息：");
                Scanner scanner = new Scanner(System.in);
                String s= scanner.nextLine();
                channel.writeAndFlush(s ).sync();
            }

        } else {
            System.out.println("Msg Send Failed , Connection Lost!");
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            TcpClient.sendMsg("02");

        } catch (Exception e) {
            System.out.println("main err:"+ e);
        }
    }


}

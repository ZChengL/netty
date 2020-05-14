package com.cof.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;

/**
 * @author lilongke
 * @ClassName UdpClient
 **/
@Slf4j
public class UdpClient {

    private Channel channel;

    public void init(String ip, int port) {

            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .remoteAddress(ip, port)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new UdpClientHandler());
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        }
                    });

            try {
                channel = bootstrap.bind(0).sync().channel();
            } catch (InterruptedException e) {
                log.error("连接失败+++++++++++++" + e);
            }

    }

    public void send(String ip, int port) {
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("abcdefg", CharsetUtil.UTF_8), new InetSocketAddress(ip, port)));
    }

    public static void main(String []args) throws InterruptedException, IOException {
        UdpClient client = new UdpClient();
        Map<String, Object> ymlMap = getYmlMap();
        int port = Integer.valueOf(((Map<String, Object>) ymlMap.get("UdpServer")).get("port").toString());
        client.init("127.0.0.1", port);

        while (true) {
            client.send("127.0.0.1", port);
            Thread.sleep(2000);
        }
    }

    public static Map<String,Object> getYmlMap(){
        Map<String,Object> obj =null;
        try {
            Yaml yaml = new Yaml();
            InputStream resourceAsStream = UdpClient.class.getClassLoader().getResourceAsStream("application.yml");
            obj = (Map) yaml.load(resourceAsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;

    }

}

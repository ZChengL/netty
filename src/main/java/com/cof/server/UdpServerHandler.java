package com.cof.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lilongke
 *  *@ClassName UdpServerHandler
 * @since 2020.05.08
 * 监听来自udp客户端的消息，并进行消息转发
 */

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    public Logger logger = LogManager.getLogger("【UdpServerHandler】"+ UdpServerHandler.class.getName());

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        //super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

        byte[] data = new byte[datagramPacket.content().readableBytes()];
        Channel channel = channelHandlerContext.channel();
        ChannelId id = channel.id();
        ByteBuf bytes = datagramPacket.content().getBytes(0, data);
        logger.info("channel id: " + id);
        Thread.sleep(3000);
        System.out.println(bytes.hashCode());
        logger.info("handler instance: " + this.hashCode());
        logger.info("UDP server recv data:"+new String(data));
//        System.out.println();
        channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("abcd", CharsetUtil.UTF_8),
                datagramPacket.sender()));
    }
}

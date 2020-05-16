package com.cof.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;


/**
 * @author lilongke
 * @ClassName UdpClientHandler
 **/
@Slf4j
public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        byte[] data = new byte[msg.content().readableBytes()];
        msg.content().getBytes(0, data);
        log.info("client read data:"+new String(data));
    }
}

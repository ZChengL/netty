package com.cof.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author lilongke
 *  *@ClassName TcpServer
 * @since 2020.05.08
 * 监听来自tcp客户端的消息，并进行消息转发
 */
@Slf4j
public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    public Logger logger = LogManager.getLogger("【TcpServerHandler】"+ TcpServerHandler.class.getName());
    /**
        * 打印接收到的内容，并回传
        * @param ctx msg
        * @return void
        */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg.equals("01")){
            log.info("receive command ：" + msg);
            ctx.channel().writeAndFlush("command 01 executed!\n");
        }else if(msg.equals("02")){
            log.info("receive command ：" + msg);
            ctx.channel().writeAndFlush("command 02 executed!\n");
        }else {
            log.info("unknown command：" + msg);
            ctx.channel().writeAndFlush("unknown command!\n");
        }
    }

    /**
     * 获取IP
     * @param ctx
     */
    private String getIP(ChannelHandlerContext ctx) {
        return StringUtils.substringAfter(ctx.channel().remoteAddress().toString(), "/");
    }

    /**
     * 新客户端接入
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String remoteIp = getIP(ctx);
        logger.info("客户端[{}]已建立连接！hashCode:{}", remoteIp, ctx.hashCode());
        super.channelActive(ctx);
//        scheduledFuture = ctx.channel().eventLoop().scheduleAtFixedRate(new Runnable() {
//                                                                            @Override
//                                                                            public void run() {
//                                                                                logger.info("定时任务执行。。。。。");
//                                                                            }
//                                                                        },  // 执行线程
//                0,  // 初始化延时
//                10, // 间隔时间
//                TimeUnit.SECONDS); // 计时单位
    }

    /**
     * 客户端断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        scheduledFuture.cancel(false);
        String remoteIp = getIP(ctx);
        logger.info("客户端[{}]断开连接！hashCode:{}", remoteIp, ctx.hashCode());
        super.channelInactive(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught! cause:" + cause.toString());
        ctx.close();
    }
}

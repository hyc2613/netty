package com.hyc.idle.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    private int readTimeoutCount = 0;
    private int totalIdleCount = 3;
    private int rcvMsgCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state) {
                case READER_IDLE:
                    readTimeoutCount++;
                    System.out.println("receive read timeout");
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
            }

            if (readTimeoutCount >= totalIdleCount) {
                ctx.channel().writeAndFlush("channel read timeout beyond 3 times, close channel");
                // 关闭该client的连接。
                ctx.channel().close();
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s = (String) msg;
        rcvMsgCount++;
        System.out.println(String.format("第%d次收到消息：%s", rcvMsgCount, s));
        // 如果是心跳检测的消息，则继续回写给客户端
        if ("hb".equals(s)) {
            ctx.writeAndFlush(s);
        }
    }

}

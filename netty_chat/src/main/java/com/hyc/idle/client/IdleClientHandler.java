package com.hyc.idle.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleClientHandler extends SimpleChannelInboundHandler<String> {

    private int idleCount = 0;
    private int totalCount = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        totalCount++;
        System.out.println(String.format("rcv msg : %d,  times: %s", totalCount, msg));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            switch (state) {
                case WRITER_IDLE:
                    idleCount++;
                    break;
                default:
                    break;
            }
            // 利用netty的write超时，这里间隔2秒就往服务端发送一个心跳信息，但是只发送3次，3次之后服务端那边就会read超时
            if (idleCount <= 3) {
                ctx.channel().writeAndFlush("hb");
            } else {
                System.out.println("心跳包发送结束，不再发送心跳请求！！！");
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

}

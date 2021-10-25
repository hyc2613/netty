package com.hyc.handler.object.client;

import com.hyc.handler.object.common.User;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.SocketAddress;

public class NettyObjectClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("jack");
        user.setSex("F");
        ctx.channel().writeAndFlush(user);
        ctx.fireChannelActive();
    }
}

package com.hyc.handler.object.server;

import com.hyc.handler.object.common.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NettyObjectServerHandler extends SimpleChannelInboundHandler<User> {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("a client has connected!"+sdf.format(new Date()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, User user) throws Exception {
        System.out.println("rcv object from "+ ctx.channel().remoteAddress() + ", object = "+user);
    }
}

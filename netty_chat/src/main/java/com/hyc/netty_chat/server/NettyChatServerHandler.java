package com.hyc.netty_chat.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class NettyChatServerHandler extends SimpleChannelInboundHandler {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户端 【"+channel.remoteAddress()+"】 已经上线了。 "+sdf.format(new Date()));
        channelGroup.add(channel);
        System.out.println(channel.remoteAddress()+" 已经上线了");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        // 因为在pipeline中使用了 String编解码，所以这里msg直接就是一个String
//        ByteBuf byteBuf = (ByteBuf) msg;
//        String s = byteBuf.toString(CharsetUtil.UTF_8);
        String s = ((String) msg);
        System.out.println("服务端收到消息："+s);
        Iterator<Channel> iterator = channelGroup.iterator();
        while (iterator.hasNext()) {
            Channel next = iterator.next();
            if (next.id().equals(channel.id())) {
                channel.writeAndFlush("自己发的消息："+s);
            } else {
                next.writeAndFlush("客户端【"+channel.remoteAddress()+"】发来消息："+s);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

}

package com.hyc.nio.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9000));
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("9000服务已启动，并已监听accept事件");

        while (true) {
            // select方法阻塞在这里，如果所监听的channel有事件发生就会响应
            selector.select();
            Set<SelectionKey> selectorKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectorKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                handle(key);
                iterator.remove();
            }
        }
    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel clientSocketChannel = channel.accept();
            clientSocketChannel.configureBlocking(false);
            clientSocketChannel.register(key.selector(), SelectionKey.OP_READ);
            System.out.println("监听read事件，等待客户端写入数据");
        } else if (key.isReadable()) {
            System.out.println("触发read事件，客户端已经写入数据。");
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            if (len < 0) {
                System.out.println("read nothing!");
            } else {
                System.out.println(new String(byteBuffer.array(), 0, len));
            }
            byteBuffer.flip();
            System.out.println("服务端往客户端回写！");
            channel.write(byteBuffer);
        }
    }

}

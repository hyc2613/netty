package com.hyc.nio.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class NioClient {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT);

        sc.connect(new InetSocketAddress(9000));

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                handle(selectionKey);
                iterator.remove();
            }
        }
    }

    private static void handle(SelectionKey selectionKey) throws Exception {
        if (selectionKey.isConnectable()) {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            if (channel.finishConnect()) {
                channel.register(selectionKey.selector(), SelectionKey.OP_READ);
                writeMessage(channel);
            }
        } else if (selectionKey.isReadable()) {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            log.info("接受到的信息： {}", new String(byteBuffer.array(), 0 , len));
        }

    }

    private static void writeMessage(SocketChannel channel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 把要写的数据put到缓冲区
        byteBuffer.put("hello, NIO.".getBytes());
        byteBuffer.flip();
        log.info("write now!");
        channel.write(byteBuffer);
        if (!byteBuffer.hasRemaining()) {
            log.info("write finish!");
        }
    }

}

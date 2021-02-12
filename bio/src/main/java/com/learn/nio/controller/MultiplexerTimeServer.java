package main.java.com.learn.nio.controller;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用类，一个独立的线程负责轮询多路复用器Selector，可以处理多个客户端的并发接入
 */

public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;
    /**
     * 初始化多路复用器、绑定监听端口
     */
    public MultiplexerTimeServer(int port) {
        try {
            //创建Reactor线程，创建多路复用器并启动线程
            selector = Selector.open();
            //打开ServerSocketSchannel，用于监听客户端的连接，它是所有客户端连接的父管道
            servChannel =ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            //绑定监听端口，设置连接为非阻塞模式
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            //将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，用来读取客户端发送的网络消息
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port :" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (IOException e) {

                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //Accept the new Connection
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            //将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读操作，用来读取客户端发送的网络消息
            sc.register(selector, SelectionKey.OP_READ);
        }
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            //异步读取客户端请求消息到缓冲区
            int readBytes = sc.read(readBuffer);
            if (readBytes > 0) {
                readBuffer.flip();
                //对ByteBuffer进行编解码，如果有半包消息指针reset
                byte[] bytes = new byte[readBuffer.remaining()];
                readBuffer.get(bytes);
                String body = new String(bytes, "UTF-8");
                System.out.println("The time server receive order : " + body);
                String currentTime = "QUERY TIME ORDER"
                        .equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                        : "BAD ORDER";
                doWrite(sc, currentTime);
            } else if (readBytes < 0) {
                //对端链路关闭
                key.cancel();
                sc.close();
            } else {
                //读到0字节，忽略
                ;
            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}

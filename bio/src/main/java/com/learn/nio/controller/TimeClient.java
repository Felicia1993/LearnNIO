package main.java.com.learn.nio.controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * nio客户端序列
 * 1.打开SocketChannel
 * 2.设置SocketChannel为非阻塞模式，同时设置TCP参数
 * 3.异步连接服务端
 * 4.判断连接结果，如果连接成功，调到步骤10，否则执行步骤5
 * 5.向Reactor线程的多路复用器注册OP_CONNECT事件
 * 6.创建Selector，启动线程
 * 7.Selector轮询就绪的key
 * 8.handerConnect()
 * 9.判断连接是否完成，完成执行步骤10
 * 10.向多路复用器注册读事件OP_READ
 * 11.handleRead()异步读请求消息到ByteBuffer
 * 12.decode请求消息
 * 13.异步写ByteBuffer到SocketChannel
 *
 */

public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient_001");
    }
}

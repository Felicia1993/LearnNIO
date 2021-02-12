package main.java.com.learn.aio.controller;

/**
 * NIO2.0进入了新的异步通道的概念，并提供了异步文件通道和异步套接字通道的实现。异步通道提供两种方式
 * 异步通道提供两种方式获取操作结果
 *  通过java.util.concurrent.Future类来表示异步操作的结果
 *  在执行异步操作的时候传入一个java.nio.channels
 *
 *  NIO2.0的异步套接字通道是真正的异步非阻塞IO，它对应UNIX网络编程中的事件驱动IO，它不需要通过多路复用器对注册的通道进行轮询操作即可实现异步读写
 */
public class TimeClientHandle {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
    }
}

package main.java.com.learn.bio.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeServerHandlerExecutePool {
    private ExecutorService executor;

    /**
     * 由于底层的通信依然采用同步阻塞模型，因此无法从根本上解决问题
     * 如果通信对方返回应答时间过长，会引起的级联故障
     * （1）服务端处理缓慢，返回应答消息耗费60s，平时只需要10ms
     * （2）采用伪异步IO的线程正在读取故障复位节点的响应，由于读取输入流是阻塞的，因此，它将会被同步阻塞60s
     * （3）假如所有的可用线程都被故障服务器阻塞，后序所有的IO消息都将队列中排队
     * （4）由于线程池采用阻塞队列实现，当队列积满之后，后续入队列的操作将被阻塞
     * （5）由于前端只有一个Accptor线程接受客户端接入，它被阻塞在线程池的同步阻塞队列之后，新的哭护短请求消息将被拒绝，客户端会发现大量的连接超时
     * （6）由于几乎所有的连接都超时，调用者会认为系统已经崩溃，无法接受新的请求消息
     * @param maxPoolSize
     * @param queueSize
     */
    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        new ThreadPoolExecutor(Runtime.getRuntime()
        .availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(TimeServerHandler timeServerHandler) {
        executor.execute(timeServerHandler);
    }
}

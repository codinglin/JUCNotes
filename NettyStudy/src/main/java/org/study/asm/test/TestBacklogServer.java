package org.study.asm.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
/**
断点方法 控制全连接队列大小 （因为netty处理能力很强，只有来不及处理了，才放入全连接队列）
在 NioEventLoop.java 文件里 的 if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) { 打断点

<b>源码里解释：</b>
1.    SelectionKey.OP_ACCEPT 事件 表示连接建立了， 表示三次握手成功了

2.    unsafe.read() 一执行 就表示：服务端执行了accept() 了，进一步：在连接队列里就可以把这个连接取出来了

3.    这里打个断点，相当于，故意把连接信息 不取出来，放在队列里


<b>方法：</b>

1.    服务端debug运行

2.    客户端直接运行连接

<b>查看 backlog初始值</b>
1.  从 java.nio.channels.ServerSocketChannel 里(ctrl+F12)找 bind(xxx, backlog) 方法
2.  鼠标右键 对着 find(xxx 点击 [Find Usages] 找到netty里使用该方法的 方法
3.  找到 config.getBacklog()); =====> config; =====>  private final ServerSocketChannelConfig config;
4.  找到 ServerSocketChannelConfig  的 int getBacklog();  =====> class DefaultServerSocketChannelConfig 里
5.  找到 private volatile int backlog = NetUtil.SOMAXCONN;
6.  最后发现 NetUtil 里 253行定义了默认数据

 */
public class TestBacklogServer {


    public static void main(String[] args) {

        final ServerBootstrap bs = new ServerBootstrap();
        /**
         * 调整 全连接队列大小 【注意这里和系统的取最小值为准，所以系统也要配】
         */
        bs.option(ChannelOption.SO_BACKLOG,5);// 超出报错：java.net.ConnectException: Connection refused: no further information
        bs.group(new NioEventLoopGroup());
        bs.channel(NioServerSocketChannel.class);
        bs.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler());
            }
        }).bind(8080);

    }

}

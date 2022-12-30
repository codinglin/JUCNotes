package org.study.rpc.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestConnectionTimeout {

    /*
    配置参数：
    1. 客户端通过 .option() 方法配置参数 给 SocketChannel 配置餐年数 (因为对于客户端来说就一个Socket)
    2. 服务端
        new ServerBootstrap().option()       // 给 ServerSocketChannel 配置参数
        new ServerBootstrap().childHandler() // 给 SocketChannel 配置参数
     */

    public static void main(String[] args) {

        final NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            final Bootstrap bs = new Bootstrap()
                    .group(group)
                        // 300毫秒超时限制 （注意：最多限制2秒，否则报更底层的java.net.ConnectException: Connection refused: no further information）
                        /*
                        debug 发现，这里是创建了 定时任务，一秒后触发，如果连接成功则取消  使用promise做主次线程之间通信
int connectTimeoutMillis = config().getConnectTimeoutMillis();
if (connectTimeoutMillis > 0) {
    connectTimeoutFuture = eventLoop().schedule(new Runnable() {
        @Override
        public void run() {
            ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
            ConnectTimeoutException cause =
                    new ConnectTimeoutException("connection timed out: " + remoteAddress);
            if (connectPromise != null && connectPromise.tryFailure(cause)) {
                close(voidPromise());
            }
        }
    }, connectTimeoutMillis, TimeUnit.MILLISECONDS);
}
                        如果超时 只执行到 future.sync() 然后 抛异常
                         */
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());
            ChannelFuture future = bs.connect("localhost", 8080);

            System.out.println("111111111111111111111111111111111111111111111111111111111111");
            final ChannelFuture channelFuture = future.sync();

            System.out.println("222222222222222222222222222222222222222222222222222222222222");
            channelFuture.channel().close().sync();

        } catch (Exception e) {

            e.printStackTrace();
            log.debug("timeout");

        } finally {
            group.shutdownGracefully();
        }

    }

}

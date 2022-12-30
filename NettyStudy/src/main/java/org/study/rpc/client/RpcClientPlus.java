package org.study.rpc.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.study.rpc.handler.RpcResponseMessageHandler;
import org.study.rpc.message.RpcRequestMessage;
import org.study.rpc.protocol.MessageCodecSharable;
import org.study.rpc.protocol.ProtocolFrameDecoder;
import org.study.rpc.protocol.SequenceIdGenerator;
import org.study.rpc.server.service.HelloService;

import java.lang.reflect.Proxy;

/*
CTRL + ALT + M 进行代码抽离封装
CTRL + ALT + L 整理代码
 */
@Slf4j
public class RpcClientPlus {

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {

/*        getChannel().writeAndFlush(
                new RpcRequestMessage(
                        1,
                        "com.rpc.server.service.HelloService",
                        "sayHello",
                        String.class,
                        new Class[]{String.class},
                        new Object[]{"helloworld!"}
                )
        );*/

        final HelloService service = getProxyService(HelloService.class);
        log.debug("11111111111111111111111111111111111111111111111111111111111111111111111111");

        service.sayHello("在不在！");
//        service.sayHello("嘿！");
    }

    /*
    #######################################################
    #################       创建代理类       ###############
    ##############   内部 把代理对象转为消息发送   ###########
    #######################################################
     */
    public static <T> T getProxyService(Class<T> serviceClass){
        // 类加载器， 代理的实现接口的数组，
        ClassLoader loader = serviceClass.getClassLoader();
        Class[] interfaces = {serviceClass};
        log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        final int nextId = SequenceIdGenerator.nextId();
        //                                                                  sayHello "你好！"
        final Object o = Proxy.newProxyInstance(loader, interfaces, (proxy,  method,  args) -> {
            // 1. 将方法调用 转为 消息对象
            final RpcRequestMessage msg = new RpcRequestMessage(
                    nextId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            log.debug("22222222222222222222222222222222222222222222222222222222222222222222222222");

            // 2. 将消息对象 发送 出去               【但是 一时半会 结果不过来】
            getChannel().writeAndFlush(msg);

            // 3. 准备一个Promise对象 来接收结果      【指定 Promise对象 【异步】接受结果的线程】  【这里不会阻塞住，所以下面得阻塞等待结果】
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(nextId, promise);
            log.debug("33333333333333333333333333333333333333333333333333333333333333333333333333");

            // 4. await()不会抛异常， 【【【 同步阻塞 等待promise的结果(成功or失败) 】】】
            promise.await();
            log.debug("55555555555555555555555555555555555555555555555555555555555555555555555555");

            if(promise.isSuccess()){
                // 调用正常
                return promise.getNow();
            }else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        });
        log.debug("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        // 返回 被代理的对象
        return (T) o;
    }

    // 初始化获取Channel 单例 + 【双重检查锁】
    public static Channel getChannel(){

        if (channel != null)return channel;

        synchronized (LOCK){
            /**
             * t1 和 t2 一起到这里
             * t1 进来 执行完了，锁放开
             * t2 被唤醒才进来
             *  此时没有这行检查 将会再初始化一次
             */
            if (channel != null)return channel;
            initChannel();
            return channel;
        }

    }

    /*
     初始化 channel
     */
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); // 【使用 asm包方法】

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_RESPONSE_HANDLER = new RpcResponseMessageHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProtocolFrameDecoder()); // 【使用 asm包方法】
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
            }
        });

        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();

            /**
             * >>>>>>>>>>>>>>>>>>>>>>>>>>>>
             * channel.closeFuture().sync() 【同步将等待 结束连接才往下执行，将不会返回channel】
             * 改造 ========================
             * 异步监听 指导 结束的动作
             * <<<<<<<<<<<<<<<<<<<<<<<<<<<<
             */
            channel.closeFuture().addListener(future -> {

                group.shutdownGracefully();
            });

        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
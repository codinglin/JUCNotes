package org.study.rpc.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class Server {

    public static void main(String[] args) {
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(LOGGING_HANDLER);

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                super.channelRead(ctx, msg);
                                ByteBuf buffer = (ByteBuf) msg;
                                log.debug(buffer.toString(Charset.defaultCharset()));

                                // 建议使用 ctx.alloc() 创建 ByteBuf
                                final ByteBuf response = ctx.alloc().buffer(); // class io.netty.buffer.PooledUnsafeDirectByteBuf
                                response.writeBytes("服务端:".getBytes());
                                response.writeBytes(buffer);
                                ctx.writeAndFlush(response);

                            }
                        });
                    }
                })
                .bind(7777);
    }


}

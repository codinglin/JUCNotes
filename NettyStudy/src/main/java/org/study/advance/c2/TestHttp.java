package org.study.advance.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        new ServerBootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        // 作为服务器，使用 HttpServerCodec 作为编码器与解码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        // 服务器只处理HTTPRequest
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) {
                                // 获得请求uri
                                log.debug(msg.uri());

                                // 获得完整响应，设置版本号与状态码
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                // 设置响应内容
                                byte[] bytes = "<h1>Hello, World!</h1>".getBytes(StandardCharsets.UTF_8);
                                // 设置响应体长度，避免浏览器一直接收响应内容
                                response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                // 设置响应体
                                response.content().writeBytes(bytes);

                                // 写回响应
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                })
                .bind(8080);
    }
}

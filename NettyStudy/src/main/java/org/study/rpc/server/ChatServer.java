package org.study.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.study.rpc.protocol.MessageCodecSharable;
import org.study.rpc.protocol.ProtocolFrameDecoder;
import org.study.rpc.server.handler.*;

/**
 * 聊天--服务端
 */
@Slf4j
public class ChatServer {



    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();
        // 局部变量
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoggingHandler           LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();   //--登录---处理器
        final ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();//--单聊---处理器
        final GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();//--创建群聊---处理器
        final GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();      //--群聊---处理器
        final QuitHandler QUIT_HANDLER = new QuitHandler();  //--断开连接---处理器


        try {
            final ServerBootstrap bs = new ServerBootstrap();
            bs.channel(NioServerSocketChannel.class);
            bs.group(boss, worker);
            bs.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 用来判断 是不是读 空闲时间过长，或写空闲时间过长 (读，写，读写空闲时间限制) 0表示不关心
                    ch.pipeline().addLast(new IdleStateHandler(12, 0, 0));
                    /*
                    ################################################################
                    #####  ChannelDuplexHandler 可以同时作为 入站 和 出站处理器  #######
                    ##### 12 秒内 没读到数据 触发   IdleState.READER_IDLE       #######
                    #####       写         触发   IdleState.WRITER_IDLE       #######
                    #####     读写         触发   IdleState.ALL_IDLE          #######
                    ################################################################
                     */
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        // 【用来处理 读写之外的 特殊的事件】
                        @Override //-- 触发的用户事件 --
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 是否 读超时
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("==============================已经12秒没读到数据了！====================================");
                                ctx.channel().close();
                            }
                        }
                    });

                    ch.pipeline().addLast(QUIT_HANDLER);               //--断开连接---处理器

                    ch.pipeline().addLast(new ProtocolFrameDecoder()); // 帧解码器 【与自定义编解码器 MessageCodecSharable一起配置参数】
                    ch.pipeline().addLast(LOGGING_HANDLER);            // 日志
                    ch.pipeline().addLast(MESSAGE_CODEC);              // 出站入站的 自定义编解码器 【 解析消息类型 】
                    // simple处理器 【针对性的对登录进行处理】 【流水线 会向上执行出站Handler,  到 ProcotolFrameDecoder(入站停止)】
                    ch.pipeline().addLast(LOGIN_HANDLER);         //--登录---处理器
                    ch.pipeline().addLast(CHAT_HANDLER);          //--单聊---处理器
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);  //--创建群聊---处理器
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);    //--群聊---处理器


                }
            });

            ChannelFuture channelFuture = bs.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            log.error("server error", e);

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}

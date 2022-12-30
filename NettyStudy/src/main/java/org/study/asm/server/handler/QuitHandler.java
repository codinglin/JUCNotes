package org.study.asm.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.study.asm.server.session.SessionFactory;

/**
 * 退出---处理器
 * 只关心  异常事件 和 ChannelInActive事件
 */
@ChannelHandler.Sharable
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {

    // 连接 断开时 触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 1. 解绑 channel
        SessionFactory.getSession().unbind(ctx.channel());

        log.debug("{} ///////////////主动断开了", ctx.channel());

    }

    // 异常断开  disconnect() 不会触发
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        // 1. 解绑 channel
        SessionFactory.getSession().unbind(ctx.channel());

        log.debug("{} ///////////////异常断开了，异常是 {}", ctx.channel(), cause);

    }

    // 新连接
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("======================= handlerAdded---------------------");
    }

    // 断开连接 disconnect 会触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("======================= handlerRemoved---------------------");
    }


}

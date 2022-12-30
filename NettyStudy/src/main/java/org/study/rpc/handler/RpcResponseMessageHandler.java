package org.study.rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.study.rpc.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
泛型通配符 Promise<?> 只能 从泛型容器里获取值，不能从泛型容器中设置值
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    /*
    未来 送货人 取到数据 往这个包里放东西
    这个状态 算有状态算共享信息，但是是使用了 ConcurrentHashMap 和 下面 remove()单步操作 所以没有线程安全问题
     */
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        // 拿到 空的 promise
//        final Promise<Object> promise = PROMISES.get(msg.getSequenceId());
        final Promise<Object> promise = PROMISES.remove(msg.getSequenceId()); // 获取 并销毁值

        if (promise != null) {

            final Object returnValue = msg.getReturnValue();         // 正常结果
            final Exception exceptionValue = msg.getExceptionValue();// 异常结果 【约定 为 null才是正常的】
            if (exceptionValue != null) {

                promise.setFailure(exceptionValue);

            }else{
                promise.setSuccess(returnValue);
            }


        }
        log.debug("44444444444444444444444444444444444444444444444444444444444444444444444444");

        log.debug("{}", msg);

    }
}

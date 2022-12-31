package org.study.rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.study.rpc.message.RpcRequestMessage;
import org.study.rpc.message.RpcResponseMessage;
import org.study.rpc.server.service.HelloService;
import org.study.rpc.server.service.ServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    // 处理调用，最后返回 RpcResponseMessage对象
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {

        final RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());

        try {
            // 上面对象里 获取【接口类】全限定名
            final Class<?> interfaceClazz = Class.forName(message.getInterfaceName());
            // 根据接口类 获取 【接口实现类】
            final HelloService service = (HelloService) ServiceFactory.getService(interfaceClazz);
            // clazz 根据 方法名和参数类型 确定 【具体方法】
            final Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            // 根据 具体方法 使用代理 【执行方法】
            final Object invoke = method.invoke(service, message.getParameterValue());

            response.setReturnValue(invoke);

        } catch (Exception e) {
            log.debug("-------------------- 出异常了 --------------------");
            e.printStackTrace();
//            response.setExceptionValue(e);                         【e.getCause() 拿到问题的起因】
            response.setExceptionValue(new Exception("远程调用出错：" + e.getCause().getMessage()));
        }

        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final RpcRequestMessage requestMsg = new RpcRequestMessage(
                        1,
                "com.rpc.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"helloworld!"}
        );
        // 上面对象里 获取【接口类】全限定名
        final Class<?> interfaceClazz = Class.forName(requestMsg.getInterfaceName());
        // 根据接口类 获取 【接口实现类】
        final HelloService service = (HelloService) ServiceFactory.getService(interfaceClazz);
        // 根据 方法名和参数类型 确定 【具体方法】
        final Method method = service.getClass().getMethod(requestMsg.getMethodName(), requestMsg.getParameterTypes());
        // 根据 具体方法 使用代理 【执行方法】
        final Object invoke = method.invoke(service, requestMsg.getParameterValue());
        System.out.println(invoke);


    }
}
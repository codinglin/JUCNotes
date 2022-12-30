package org.study.asm.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.study.asm.config.Config;
import org.study.asm.message.LoginRequestMessage;
import org.study.asm.message.Message;
import org.study.asm.protocol.MessageCodecSharable;

public class TestAlgorithm {

    public static void main(String[] args) {

        // 局部变量
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

        final EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                LOGGING_HANDLER,

//                new ProcotolFrameDecoder(),   // 帧解码器 【与自定义编解码器 MessageCodecSharable一起配置参数】
                MESSAGE_CODEC,

                LOGGING_HANDLER
        );

        LoginRequestMessage message = new LoginRequestMessage("张三", "123456");

        /* ########################################################
           #######    出站测试  【出站 自动编码】  encode   ##########
           ########################################################*/
//        embeddedChannel.writeOutbound(message);

        /* #########################################################
           #######    入站测试   【入站 自动解码】  decode ############
           #########################################################*/
        final ByteBuf buf = messageToByteBuf(message);
        embeddedChannel.writeInbound(buf);


    }

    private static ByteBuf messageToByteBuf(Message msg)
    {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();

        out.writeBytes(new byte[]{1,2,3,4}); // 4字节的 魔数
        out.writeByte(1);                    // 1字节的 版本
        out.writeByte(Config.getMySerializerAlgorithm().ordinal()); // 1字节的 序列化方式 0-jdk,1-json
        out.writeByte(msg.getMessageType()); // 1字节的 指令类型
        out.writeInt(msg.getSequenceId());   // 4字节的 请求序号 【大端】
        out.writeByte(0xff);                 // 1字节的 对其填充，只为了非消息内容 是2的整数倍


        final byte[] bytes = Config.getMySerializerAlgorithm().serializ(msg);

        // 写入内容 长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

        return out;
    }

}

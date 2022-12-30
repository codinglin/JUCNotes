package org.study.rpc.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.study.rpc.config.Config;
import org.study.rpc.message.LoginRequestMessage;
import org.study.rpc.message.Message;
import org.study.rpc.protocol.MessageCodecSharable;

public class TestMessageCodec {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(),
                new MessageCodecSharable()
        );
        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);

        // decode
        final ByteBuf buf = messageToByteBuf(message);
        channel.writeInbound(buf);
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

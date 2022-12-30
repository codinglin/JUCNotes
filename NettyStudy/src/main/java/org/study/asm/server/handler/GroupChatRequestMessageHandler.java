package org.study.asm.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.study.asm.message.GroupChatRequestMessage;
import org.study.asm.message.GroupChatResponseMessage;
import org.study.asm.server.session.GroupSession;
import org.study.asm.server.session.GroupSessionFactory;

import java.util.List;

/**
 * 群聊---管理器
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {

        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        final List<Channel> channelList = groupSession.getMembersChannel(msg.getGroupName());

        for (Channel  channel : channelList){

            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));

        }

    }

}

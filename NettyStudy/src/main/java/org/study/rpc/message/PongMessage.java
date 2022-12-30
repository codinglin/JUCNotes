package org.study.rpc.message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return Message.PongMessage;
    }
}

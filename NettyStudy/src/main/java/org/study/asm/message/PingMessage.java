package org.study.asm.message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}

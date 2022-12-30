package org.study.asm.server.session;

public abstract class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    public static GroupSession getGroupSession() {
        return session;
    }
}

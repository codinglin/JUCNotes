package org.study.asm.server.session;

public abstract class SessionFactory {

    private static Session session = new SessionMemoryImpl();

    public static Session getSession() {
        return session;
    }
}

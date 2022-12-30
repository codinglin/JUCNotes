package org.study.asm.server.service;

public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    public static UserService getUserService()
    {
        return userService;
    }


}

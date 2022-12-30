package org.study.asm.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {

    @Override
    public boolean login(String username, String password) {

        final String pass = allUserMap.get(username);
        if (pass == null)
        {
            return false;
        }

        return pass.equals(password);
    }


    private Map<String, String> allUserMap = new ConcurrentHashMap<>();
    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("zhaoliu", "123");
        allUserMap.put("qianqi", "123");
    }
}

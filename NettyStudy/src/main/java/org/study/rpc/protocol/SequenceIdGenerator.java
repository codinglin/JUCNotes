package org.study.rpc.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/*
抽象类 不能实例化
 */
public abstract class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();

    // 递增 和 获取
    public static int nextId(){ return id.incrementAndGet(); }

}

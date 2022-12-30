package org.study.asm.test;

import java.util.*;

public class OnlyTest {

    public static void main(String[] args) {

/*
        // 【例题 1】
        System.out.println(Config.getServerPort());

        final int ordinal = Config.getMySerializerAlgorithm().ordinal();
        System.out.println("ordinal = " + ordinal);

        String s  = "aaaaaaaaaa";
        final byte[] bytes = Config.getMySerializerAlgorithm().serializ(s);

        System.out.println(Arrays.toString(bytes));
*/

        // 【例题 2】
//        System.out.println(MySerializer.Algorithm.Json.ordinal());



        // 【例题 3】 测试 集合的 computeIfPresent  如果存在key则计算
        final Map<String, Set<String>> map = new HashMap<>();

        final HashSet<String> set1 = new HashSet<>();
        set1.add("张三");set1.add("李四");set1.add("王五");
        map.put("ql1", set1);

        final HashSet<String> set2 = new HashSet<>();
        set2.add("zhangsan");set2.add("lisi");set2.add("wangwu");

        map.put("ql1", set1);
        map.put("ql2", set2);

        final boolean re = map.get("ql1").remove("abc");
        System.out.println(re); // true or false
// <<<<<<<<<<<<<<<<<
//        map.get("ql6").add("赵六");
// =================
        map.computeIfPresent("ql6", (k, v)->{
            v.add("赵六");
            return v;
        });
// >>>>>>>>>>>>>>>>>



        System.out.println(map.toString());


    }


}

package com.huawei.dubbo.common.util;

import java.util.Random;

public class ExceptionUtils {
    private static final Random random = new Random();

    /**
     * 概率性抛出異常
     * @param errRate 失敗率。 百分比
     */
    public static void addRuntimeException(int errRate) {
        if (random.nextInt(100) < errRate) {
            throw new RuntimeException("Add a potential exception");
        }
    }
}

/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */

package com.huawei.common.error;

import java.util.Random;

public class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static void addRuntimeException(int errRate) {
        if (new Random().nextInt(100) < errRate) {
            throw new RuntimeException("Add a Potential Exception");
        }
    }
}

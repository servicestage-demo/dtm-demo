package com.huawei.dtm.invoke.utils;

import java.util.Scanner;

public class CmdUtils {

    private static final Scanner scanner = new Scanner(System.in);

    public static int readCmd(int limits) {
        String userInput;

        do {
            userInput = scanner.nextLine();
            try {
                int intCmd = Integer.parseInt(userInput);
                if (intCmd < 0 || limits <= intCmd) {
                    println("[%s] 不是合法输入 ！ 请重新输入", userInput);
                    continue;
                }
                return intCmd;
            } catch (Throwable e) {
                println("[%s] 不是合法输入 ！ 请重新输入", userInput);
            }
        } while (true);
    }

    public static void println(Object info) {
        System.out.println(info.toString());
    }

    public static void println(String format, String... args) {
        System.out.printf((format) + "%n", args);
    }
}

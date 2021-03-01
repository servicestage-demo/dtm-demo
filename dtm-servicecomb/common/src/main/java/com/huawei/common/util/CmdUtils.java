package com.huawei.common.util;

import java.util.Random;
import java.util.Scanner;

public final class CmdUtils {
    private CmdUtils() {
    }

    private static Scanner scanner = new Scanner(System.in);

    public static int getRandomNum(int num) {
        Random random = new Random();
        return random.nextInt(num);
    }

    /**
     * 输出字符串. 后续这里可以做一下存储.
     *
     * @param info 信息
     */
    public static void println(Object info) {
        if (info == null) {
            return;
        }
        printlnToTerminal(info.toString());
        //        printlnToFile(info.toString());
    }

    public static void println(String format, String... args) {
        println(String.format(format, args));
    }

    private static void printlnToTerminal(String info) {
        System.out.println(info);
    }

    public static int readCmd(int limits) {
        String userInput;
        do {
            userInput = scanner.nextLine();
            try {
                int intCmd = Integer.parseInt(userInput);
                if (intCmd < 0 || limits <= intCmd) {
                    println(String.format("[%s] 不是合法输入! 请重新输入", userInput));
                    continue;
                }
                return intCmd;
            } catch (NumberFormatException e) {
                println(String.format("[%s] 不是合法输入! 请重新输入", userInput));
            }
        } while (true);
    }
}

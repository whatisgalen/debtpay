package com.galen;

public class Test {

    public static void main(String[] args) {
        float moneyFlt = 100.11111111f;
        System.out.println(moneyFlt);
        String formdFlt = formatDecimal(moneyFlt);
        System.out.println(formdFlt);

    }

    private static String formatDecimal(float number) {
        float epsilon = 0.01f; // 4 tenths of a cent??
        if (Math.abs(Math.round(number) - number) < epsilon) {
            return String.format("%10.0f", number); // sdb
        } else {
            return String.format("%10.2f", number); // dj_segfault
        }
    }
}

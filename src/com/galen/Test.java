package com.galen;

import java.util.Arrays;
import java.util.Random;

public class Test {

    public static void main(String[] args) {
//        float moneyFlt = 100.11111111f;
//        System.out.println(moneyFlt);
//        String formdFlt = formatDecimal(moneyFlt);
//        System.out.println(formdFlt);
        double test1 = (Math.log(10*((0.05/12)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05/12))))));
        double test2 = (Math.log(10*((0.05/12)))) / ((((Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05/12))))));
        double test3 = (Math.log(10*((0.05/12)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(((1+(0.05/12))))));
        double test4 = (Math.log(10*((0.05/12)))) / (((Math.log(Math.pow((1+(0.05)),2)))-(Math.log((1+(0.05))))));
        double test5 = (Math.log(10*((0.05/12)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05))))));
        double test6 = (Math.log(10*((0.05/12)))) / (((Math.log(Math.pow((1+(0.05)),2)))-(Math.log((1+(0.05/12))))));

        //winner
        double test7 = (-1*((Math.log(10*((0.05)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05/12))))))));


        double test8 = (Math.log(10*((0.05)))) / ((((Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05/12))))));
        double test9 = (Math.log(10*((0.05)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(((1+(0.05/12))))));
        double test10 = (Math.log(10*((0.05)))) / (((Math.log(Math.pow((1+(0.05)),2)))-(Math.log((1+(0.05))))));
        double test11 = (Math.log(10*((0.05)))) / (((Math.log(Math.pow((1+(0.05/12)),2)))-(Math.log((1+(0.05))))));
        double test12 = (Math.log(10*((0.05)))) / (((Math.log(Math.pow((1+(0.05)),2)))-(Math.log((1+(0.05/12))))));

        //166.702
//        System.out.println(test1);
//        System.out.println(test2);
//        System.out.println(test3);
//        System.out.println(test4);
//        System.out.println(test5);
//        System.out.println(test6);
//        System.out.println("******");
//        System.out.println(test7);
//        System.out.println(test8);
//        System.out.println(test9);
//        System.out.println(test10);
//        System.out.println(test11);
//        System.out.println(test12);

        double r = 1+(0.025/12);
        //compounding
        //19.034 >>> want: 18.82
        double original = ((Math.log(5))-(Math.log(8))) / ((Math.log(40))-(Math.log(41))); //not cmpd
        double otherTest1 = ((Math.log(5))-(Math.log(8))) / ((12*(Math.log(480)))-(12*(Math.log(481)))); //is cmpd
        double otherTest2 = ((Math.log(5))-(Math.log(8))) / ((12*(Math.log((Math.pow(r,2)))))-(12*(Math.log(r))));
        //so weird! why does this work???

//        System.out.println(original);
//        System.out.println(otherTest1);
//        System.out.println(otherTest2);

        int[] array = new int[10];
        Random rand = new Random();
        for (int i = 0; i < array.length; i++)
            array[i] = rand.nextInt(100) + 1;
        Arrays.sort(array);
        System.out.println(Arrays.toString(array));
// in reverse order
        for (int i = array.length - 1; i >= 0; i--)
            System.out.print(array[i] + " ");
        System.out.println();

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

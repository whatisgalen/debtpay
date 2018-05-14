package com.galen;


import java.util.ArrayList;
import java.util.Arrays;

public class Combo {
    public ArrayList<double[]> validCombos = new ArrayList<double[]>();


    public Combo() {
        this.validCombos = validCombos;
    }

    public ArrayList<double[]> getValidCombos() {
        return validCombos;
    }

    public void setValidCombos(ArrayList<double[]> validCombos) {
        this.validCombos = validCombos;
    }

    public static void main(String[] args) {
////        int n = user1.debts.size();
//        short n = 3;
//        short money = 414; //@n=3 max=414
//        short range = (short) (money - (n-1));
//
//        //starting values of each variable
////        byte d1 = range;
//        short d2 = range;
//        short d3 = 1;
//        short d4 = 1;
//
//        //arrays to contain all possible values in range
////        byte[] dd1 = new byte[range];
//        short[] dd2 = new short[range];
//        short[] dd3 = new short[range];
//        short[] dd4 = new short[range];
//
//        for (short k=0;k<range;k++) {
//            //populate each array with all possible values in range
////            dd1[k] = (byte) (d1-k);
//            dd2[k] = (short) (d2+k);
//            dd3[k] = (short) (d3+k);
//            dd4[k] = (short)(d4+k);
//        }
//        ArrayList<short[]> comboTable = new ArrayList<short[]>();
////        for (byte w=0;w<range;w++) {
////            byte[] row = new byte[n];
////            byte ddigit1 = dd1[w];
//            for (short x=0;x<range;x++) {
//                short[] row = new short[n];
//                short ddigit2 = dd2[x];
//                for (short y=0;y<range;y++) {
//                    row = new short[n];
//                    short ddigit3 = dd3[y];
//                    for (short z=0;z<range;z++) {
//                        row = new short[n];
//                        short ddigit4 = dd4[z];
////                        row[0] = ddigit1;
//                        row[0] = ddigit2;
//                        row[1] = ddigit3;
//                        row[2] = ddigit4;
//                        comboTable.add(row);
//                    }
//                }
//            }
////        }
//
////        System.out.println("comboTable for loop");
//        for (int i=0;i<50;i++) {
//            System.out.println(Arrays.toString(comboTable.get(i)));
//        }
//        System.out.println("Combinations: "+comboTable.size());
//
//
//
//
    }
}

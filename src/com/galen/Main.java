package com.galen;

import java.util.*;

public class Main {

    public static Scanner inputStream = new Scanner(System.in);

    //compounding frequency per year
    final static short CF = 12; //should be 12 for monthly || 1 for annually; 365 not ready


    public static void main(String[] args) {

//        User user = createUser();
        User user = new User("galen",3000.f);
//        getDebtsFromUser(user);

        user.addDebt("Student Debt", 16000, 0.0584d);
        user.addDebt("Cheap Car loan", 9400, 0.04d);
        user.addDebt("Misc", 20000, 0.065d);
//        user.addDebt("DebtFour", 3000, 0.04d);
//        user.addDebt("Mortgage",200000,0.015d); //this will have to wait for the memory problem
//        System.out.println("debts are: " + galen.debts);
        user.setMoney(3000.0f);

        byte totalDebts = (byte) user.debts.size();
        float[] mmpList = calculateMMPList(user);
        long startTime = System.nanoTime();
        double nanoToSec = Math.pow(10, -9);

        ArrayList<float[]> comboTable = generateCombos2(totalDebts,mmpList,user);

        //fix this async try/catch block to wait until above is done
        try {
            presentData(user, comboTable.get(0));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No combos available because not enough money!");
        }

//        for (int debtNumber = 0; debtNumber < totalDebts; debtNumber++) {
//            int totalPeriods = getPeriods(user,comboTable.get(0),debtNumber);
//            System.out.println();
//        }

        //timer (sec)
//        long endTime = System.nanoTime();
//        long totalTime = endTime - startTime;
//        double totalDubTime = totalTime * nanoToSec;
//        System.out.println(totalDubTime);
    }
    //^^^Main method closing bracket

    //Callable method that takes mmp's as inputs and returns a float of totalIntr
    public static float calculateComboIntr(float[] row, User user) {

        float totalIntr = 0;
        byte totalDebts = (byte) user.debts.size();

        for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {

//            System.out.println("Starting with debt#"+debtNumber);
            //reset accruedIntr & period for this debt
            float accruedIntr = 0;
            int period = 0;

            //accruedBal starts = principal then accrues interest during repayment
            float accruedBal = (float) user.debts.get(debtNumber).getPrincipal();

            //interestRate of the current debt in the loop
            float rate = (float) user.debts.get(debtNumber).getInterestRate();
            float r = (rate / CF);

            //grab mmp from corresponding index in input array
            float mmp = row[debtNumber], roundIntr, miniIntr1, miniIntr2, lastTransaction;

            //declare an array to log all payment transactions against debt
            ArrayList<Float> thisTransactionArray = new ArrayList<Float>();

            //loop to get accruedIntr for this debt
            while (accruedBal > 0) {

                //case 1: first period of repayment
                if (period == 0) {

                    //save some time
                    miniIntr1 = accruedBal * r;
                    miniIntr2 = (Math.round(miniIntr1 * 100));
                    roundIntr = (miniIntr2 / 100);
                    accruedBal += (roundIntr - mmp); //subtracting intr from mmp; mmp > intr

                    thisTransactionArray.add(accruedBal);
                    accruedIntr += roundIntr;
                    period++;

                    //case 2: any period after the first
                } else {

                    //accrue interest over last period's balance, then deduct mmp
                    lastTransaction = thisTransactionArray.get(period - 1);
                    miniIntr1 = lastTransaction * r;
                    miniIntr2 = (Math.round(miniIntr1 * 100));
                    roundIntr = (miniIntr2 / 100);
                    accruedBal += (roundIntr - mmp);

                    //log new balance in current period
                    thisTransactionArray.add(accruedBal);
                    accruedIntr += roundIntr;

                    //end the while loop if debt paid off, add accruedIntr from this debt to totalIntr
                    if (accruedBal <= 0)
                        totalIntr += accruedIntr;
                    else
                        period++;
                }
            } //while looping through thisTransactionArray (period++)
        } //for looping through debts (debtNumber++)
//        System.out.println(Arrays.toString(row)+" & "+totalIntr);
        return totalIntr;
    }

    public static float calculateComboIntr2(float[] row, User user) {
        float totalIntr = 0;
        byte totalDebts = (byte) user.debts.size();

        for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {

//            System.out.println(">>>>>>>>>>>>>>>>");
//            System.out.println("debt# "+(debtNumber+1)+" / "+user.debts.get(debtNumber).getDebtName());
//            System.out.println("$"+user.debts.get(debtNumber).getPrincipal()+" / "+(user.debts.get(debtNumber).getInterestRate()*100)+"%");
//            System.out.println("Starting with debt#"+debtNumber);
            //reset accruedIntr
            float accruedIntr = 0;

            //reset principal for this debt
            float princip = (float) user.debts.get(debtNumber).getPrincipal();

            //interestRate of the current debt in the loop
            float rate = (float) user.debts.get(debtNumber).getInterestRate();
            float r = 1+(rate/ CF); //e.g. 1+(0.05/12)


            //grab mmp from corresponding index in input array
            float mmp = row[debtNumber];

            //ln(princip/mmp * (r-1) ) / (ln(r^2) - ln(r) ) = x-intercept of balance function, i.e. when y=0
//            double periodsToZeroBal = (-1*((Math.log((princip/mmp)*((rate)))) / (((Math.log(Math.pow(r,2)))-(Math.log(r))))));
            double periodsToZeroBal = ((Math.log((mmp/(r-1))/(-1*(princip-(mmp/(r-1)))))) / (((CF*(Math.log(Math.pow(r,2))))-(CF*(Math.log(r))))));
//            System.out.println("debt combo:"+(Arrays.toString(row)));
//            System.out.println("periods to zero:"+periodsToZeroBal);
            periodsToZeroBal = Math.ceil(periodsToZeroBal);

            //need to find summation of this
            //calc finds accruedIntr at each period
            for (int j = 1; j <= (periodsToZeroBal+0) && accruedIntr >= 0; j++) {
                accruedIntr += (((princip * (float) (Math.pow(r, ((j) - 1)))) - (mmp * (((float) (Math.pow(r, (j - 1))) - 1) / (r - 1)))) * (rate));
//                accruedIntr += (((princip-(mmp/rate))*(Math.pow(r,((12*j)-1))) +(mmp/rate))*rate);

            }
            //e.g. ( 7500 * (1 + rate/12)^12x-1 ) - ( 500 *

            totalIntr += accruedIntr;
//            System.out.println("accruedIntr for this debt= "+accruedIntr);
        } //for looping through debts (debtNumber++)

//        System.out.println("totalIntr = $"+totalIntr);
        return totalIntr;
    }

    public static float calculateComboPeriods(float[] row, User user) {
        float totalPeriods = 0;
        byte totalDebts = (byte) user.debts.size();

        for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {

            //reset principal for this debt
            float princip = (float) user.debts.get(debtNumber).getPrincipal();

            //interestRate of the current debt in the loop
            float rate = (float) user.debts.get(debtNumber).getInterestRate();
            float r = 1+(rate/ CF); //e.g. 1+(0.05/12)


            //grab mmp from corresponding index in input array
            float mmp = row[debtNumber];

            //ln(princip/mmp * (r-1) ) / (ln(r^2) - ln(r) ) = x-intercept of balance function, i.e. when y=0
//            double periodsToZeroBal = (-1*((Math.log((princip/mmp)*((rate)))) / (((Math.log(Math.pow(r,2)))-(Math.log(r))))));
            double periodsToZeroBal = ((Math.log((mmp/(r-1))/(-1*(princip-(mmp/(r-1)))))) / (((CF*(Math.log(Math.pow(r,2))))-(CF*(Math.log(r))))));
            totalPeriods += periodsToZeroBal;

        } //for looping through debts (debtNumber++)
        return totalPeriods;
    }

    private static float calculateHelperPeriods(float[] row, byte debtNumberA, byte debtNumberB, User user) {
        float totalPeriods = 0;

        //reset principal for this debt
        float princip = (float) user.debts.get(debtNumberA).getPrincipal();

        //interestRate of the current debt in the loop
        float rate = (float) user.debts.get(debtNumberA).getInterestRate();
        float r = 1+(rate/ CF); //e.g. 1+(0.05/12)

        //grab mmp from corresponding index in input array
        float mmp = row[0];

        //ln(princip/mmp * (r-1) ) / (ln(r^2) - ln(r) ) = x-intercept of balance function, i.e. when y=0
//            double periodsToZeroBal = (-1*((Math.log((princip/mmp)*((rate)))) / (((Math.log(Math.pow(r,2)))-(Math.log(r))))));
        double periodsToZeroBal = ((Math.log((mmp/(r-1))/(-1*(princip-(mmp/(r-1)))))) / (((CF*(Math.log(Math.pow(r,2))))-(CF*(Math.log(r))))));
        totalPeriods += periodsToZeroBal;


        //reset principal for this debt
        princip = (float) user.debts.get(debtNumberB).getPrincipal();

        //interestRate of the current debt in the loop
        rate = (float) user.debts.get(debtNumberB).getInterestRate();
        r = 1+(rate/ CF); //e.g. 1+(0.05/12)

        //grab mmp from corresponding index in input array
        mmp = row[1];

        //ln(princip/mmp * (r-1) ) / (ln(r^2) - ln(r) ) = x-intercept of balance function, i.e. when y=0
//            double periodsToZeroBal = (-1*((Math.log((princip/mmp)*((rate)))) / (((Math.log(Math.pow(r,2)))-(Math.log(r))))));
        periodsToZeroBal = ((Math.log((mmp/(r-1))/(-1*(princip-(mmp/(r-1)))))) / (((CF*(Math.log(Math.pow(r,2))))-(CF*(Math.log(r))))));
        totalPeriods += periodsToZeroBal;

        return totalPeriods;
    }

    public static void sortByTotalIntr(ArrayList<float[]> comboTable, byte columns) {

        Collections.sort(comboTable, (a, b) -> Float.compare(a[columns-1], b[columns-1]));
    }

    public static void sortByPeriods(ArrayList<float[]> comboTable, byte columns) {

        Collections.sort(comboTable, (a, b) -> Float.compare(a[columns-1], b[columns-1]));
    }

//    public static void sortByYear(List<String> dates) {
//
//
//        Collections.sort(dates, (a, b) -> Integer.compare((a.charAt(7), b.charAt(7))));
//    }


//    public static void trimComboTable(ArrayList<float[]> comboTable) {
//        if (comboTable.size() > 1) //remember: greater than
//            comboTable.remove(1);
//    }

    public static ArrayList<float[]> generateCombos(byte totalDebts, float[] mmpList, User user ) {

        long startTime2 = System.nanoTime();

        byte columns = (byte) (totalDebts + 1); //1 per debt + 1 for the combo's totalIntr
        short money = (short) user.getMoney();
        short range = (short) (money - (columns - 2)); //e.g. if money=500, range = 500 - (columns-2) == 497
        ArrayList<float[]> comboTable = new ArrayList<float[]>();

        if (totalDebts == 4) {
            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];
            short mmp3 = (short) mmpList[2];
            short mmp4 = (short) mmpList[3];
            float sampleMoney = money - (mmp1+mmp2+mmp3+mmp4);
            sampleMoney = sampleMoney/4;
            //each mmp has an even share of the remaining sample money
            float[] sampleArray = {mmp1+sampleMoney,mmp2+sampleMoney,mmp3+sampleMoney,mmp4+sampleMoney};
            float sampleInterest = calculateComboIntr(sampleArray,user); //think of this like a median interest

            //arrays to contain all possible values in range
            short[] mmp1Array = new short[range];
            short[] mmp2Array = new short[range];
            short[] mmp3Array = new short[range];
            short[] mmp4Array = new short[range];

            for (short i = 0; ++i <= range;) {
                //populate each array with all possible values in range (e.g. 5-297
                if ((mmp1 + i) <= range)
                    mmp1Array[i] = (short) (mmp1 + i);
                if ((mmp2 + i) <= range)
                    mmp2Array[i] = (short) (mmp2 + i);
                if ((mmp3 + i) <= range)
                    mmp3Array[i] = (short) (mmp3 + i);
                if ((mmp4 + i) <= range)
                    mmp4Array[i] = (short) (mmp4 + i);
            }

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            short miniRange1 = (short) (range - mmp1);
            for (short z = 0; ++z <= miniRange1;) {
                float[] row = new float[columns];
                short mmp1Entry = mmp1Array[z];
                //column2 of comboTable
                short miniRange2 = (short) (range - mmp2);
                float[] testArray1 = {mmp1Entry, sampleArray[1], sampleArray[2], sampleArray[3]};
                if (calculateComboIntr(testArray1, user) <= sampleInterest) {
                    for (short y = 0; ++y <= miniRange2; ) {
                        row = new float[columns];
                        short mmp2Entry = mmp2Array[y];
                        //column3 of comboTable
                        short miniRange3 = (short) (range - mmp3);
                        float[] testArray2 = {sampleArray[0], mmp2Entry, sampleArray[2], sampleArray[3]};
                        if (calculateComboIntr(testArray2, user) <= sampleInterest) {
                            for (short x = 0; ++x <= miniRange3; ) {
                                row = new float[columns];
                                short mmp3Entry = mmp3Array[x];
                                //column 4 of comboTable
                                short miniRange4 = (short) (range - mmp4);
                                float[] testArray3 = {sampleArray[0], sampleArray[1], mmp3Entry, sampleArray[3]};
                                if (calculateComboIntr(testArray3, user) <= sampleInterest) {
                                    for (short w = 0; ++w <= miniRange4; ) {
                                        row = new float[columns];
                                        short mmp4Entry = mmp4Array[w];

//                                        float totalIntr = 0;
                                        row[0] = mmp1Entry;
                                        row[1] = mmp2Entry;
                                        row[2] = mmp3Entry;
                                        row[3] = mmp4Entry;
                                        float rowSum = row[0] + row[1] + row[2] + row[3];

                                        //filter#1: sum of mmp's must == budget
                                        if (rowSum == money) {

                                            //calculate totalIntr and set as row[3]
                                            if (comboTable.size() > 1) {
                                                if ((row[4] = calculateComboIntr(row, user)) <= comboTable.get(0)[4]) {
                                                    comboTable.add(row);
                                                    sortByTotalIntr(comboTable, columns);
                                                    comboTable.remove(2);
                                                }
                                            } else {
                                                row[4] = calculateComboIntr(row, user);
                                                comboTable.add(row);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } //close combo for loop

        } else if (totalDebts == 3) {

            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];
            short mmp3 = (short) mmpList[2];
            float sampleMoney = money - (mmp1+mmp2+mmp3);
            sampleMoney = sampleMoney/3;
            float[] sampleArray = {mmp1+sampleMoney,mmp2+sampleMoney,mmp3+sampleMoney};
            float sampleInterest = calculateComboIntr(sampleArray,user);

            //arrays to contain all possible values in range
            short[] mmp1Array = new short[range];
            short[] mmp2Array = new short[range];
            short[] mmp3Array = new short[range];

            for (short i = 0; ++i <= range;) {
                //populate each array with all possible values in range
                if ((mmp1 + i) <= range)
                    mmp1Array[i] = (short) (mmp1 + i);
                if ((mmp2 + i) <= range)
                    mmp2Array[i] = (short) (mmp2 + i);
                if ((mmp3 + i) <= range)
                    mmp3Array[i] = (short) (mmp3 + i);
            }

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            short miniRange1 = (short) (range - mmp1);
            for (short z = 0; ++z <= miniRange1;) {
                float[] row = new float[columns];
                short mmp1Entry = mmp1Array[z];
                //column2 of comboTable
                short miniRange2 = (short) (range - mmp2);
                float[] testArray1 = {mmp1Entry, sampleArray[1], sampleArray[2]};
                if (calculateComboIntr(testArray1, user) <= sampleInterest) {
                    for (short y = 0; ++y <= miniRange2; ) {
                        row = new float[columns];
                        short mmp2Entry = mmp2Array[y];
                        //column3 of comboTable
                        short miniRange3 = (short) (range - mmp3);
                        float[] testArray2 = {sampleArray[0], mmp2Entry, sampleArray[2]};
                        if (calculateComboIntr(testArray2, user) <= sampleInterest) {
                            for (short x = 0; ++x <= miniRange3; ) {
                                row = new float[columns];
                                short mmp3Entry = mmp3Array[x];

//                                float totalIntr = 0;

                                row[0] = mmp1Entry;
                                row[1] = mmp2Entry;
                                row[2] = mmp3Entry;
                                float rowSum = row[0] + row[1] + row[2];

                                //filter#1: sum of mmp's must == budget
                                if (rowSum == money) {

                                    //this is my major timesuck
                                    //calculate totalIntr and set as row[3]
                                    if (comboTable.size() > 1) {
                                        if ((row[3] = calculateComboIntr(row, user)) <= comboTable.get(0)[3]) {
                                            comboTable.add(row);
                                            sortByTotalIntr(comboTable, columns);
                                            comboTable.remove(2);
                                        }
                                    } else {
                                        row[3] = calculateComboIntr(row, user);
                                        comboTable.add(row);
                                    }
                                }
                            }
                        }
                    }
                } //close combo for loop
            }

        } else if (totalDebts == 2) {

            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];

            //arrays to contain all possible values in range
            short[] mmp1Array = new short[range];
            short[] mmp2Array = new short[range];

            for (short i = 0; i < range; i++) {
                //populate each array with all possible values in range
                if ((mmp1 + i) <= range)
                    mmp1Array[i] = (short) (mmp1 + i);
                if ((mmp2 + i) <= range)
                    mmp2Array[i] = (short) (mmp2 + i);
            }

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            for (short z = 0; ++z <= (range - mmp1);) {
                float[] row = new float[columns];
                short mmp1Entry = mmp1Array[z];
                //column2 of comboTable
                for (short y = 0; ++y <= (range - mmp2);) {
                    row = new float[columns];
                    short mmp2Entry = mmp2Array[y];

//                    float totalIntr = 0;

                    row[0] = mmp1Entry;
                    row[1] = mmp2Entry;
                    float rowSum = row[0] + row[1];

                    //filter#1: sum of mmp's must == budget
                    if (rowSum == money) {

                        //calculate totalIntr and set as row[3]
                        if (comboTable.size() > 1) {
                            if ((row[2] = calculateComboIntr(row, user)) <= comboTable.get(0)[2]) {
                                comboTable.add(row);
                                sortByTotalIntr(comboTable, columns);
                                comboTable.remove(2);
                            }
                        } else {
                            row[2] = calculateComboIntr(row, user);
                            comboTable.add(row);
                        }
                    }
                }
            } //close combo for loop

        } else {
            System.out.println("Debt quantity out of range. Please enter between 2 and 4 debts.");
        }

//        for (int k = 0; k < comboTable.size();k++) {
//            sortByTotalIntr(comboTable, columns);
//
//        }

//        sortByTotalIntr(comboTable, columns);

        double nanoToSec2 = Math.pow(10, -9);
        long endTime2 = System.nanoTime();
        long totalTime2 = endTime2 - startTime2;
        double totalDubTime2 = totalTime2 * nanoToSec2;
        System.out.println(totalDubTime2);

        return comboTable;
    } //close method

    private static ArrayList<float[]> generateCombosHelper(byte debtNumberA, short mmpA, byte debtNumberB, short mmpB, User user ) {


        //here I want to take in 2 debts at a time, get the ideal payments (still with the same money)
        //then compare the payment amounts, set a max threshold
        byte debtCount = 2;
        short moneys = (short) user.getMoney();
        short range = (short) (moneys - 1); //e.g. if money=500, range = 500 - (columns-2) == 497

        ArrayList<float[]> helperTable = new ArrayList<float[]>();

        //starting values of each variable = mmp for that debt
        short mmp1 = mmpA, mmp2 = mmpB, mmp1Entry, mmp2Entry;

        //arrays to contain all possible values in range
        short[] mmp1Array = new short[range];
        short[] mmp2Array = new short[range];

        for (short i = 0; i < range; i++) {
            //populate each array with all possible values in range
            if ((mmp1 + i) <= range)
                mmp1Array[i] = (short) (mmp1 + i);
            if ((mmp2 + i) <= range)
                mmp2Array[i] = (short) (mmp2 + i);
        }

        float[] row;
        float rowSum;
        //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
        for (short z = 0; z <= (range - mmp1); z++) {
            row = new float[3];
            mmp1Entry = mmp1Array[z];
            //column2 of comboTable
            for (short y = 0; y <= (range - mmp2); y++) {
                row = new float[3];
                mmp2Entry = mmp2Array[y];

                row[0] = mmp1Entry;
                row[1] = mmp2Entry;
                rowSum = row[0] + row[1];

                //filter#1: sum of mmp's must == budget
                if (rowSum == moneys) {

                    //calculate totalIntr and set as row[3]
                    if (helperTable.size() > 0) {
                        if ((row[2] = calculateHelperPeriods(row, debtNumberA, debtNumberB, user)) <= helperTable.get(0)[2]) {
                            helperTable.add(row);
                            sortByPeriods(helperTable, ((byte) (3)));
                            helperTable.remove(1);
                        }
                    } else {
                        row[2] = calculateHelperPeriods(row, debtNumberA, debtNumberB, user);
                        helperTable.add(row);
                    }
                }
            } //close combo for loop

//                System.out.println("now with interest as last index");
//                float[] hh = helperTable.get(0);
//                hh[(hh.length - 1)] = calculateComboIntr(hh, user);

        }
//        System.out.println(Arrays.toString(helperTable.get(0)));
    return helperTable;
    } //close method

    public static ArrayList<float[]> generateCombos2(byte totalDebts, float[] mmpList, User user ) {

        long startTime2 = System.nanoTime();

        byte columns = (byte) (totalDebts + 1); //1 per debt + 1 for the combo's totalIntr
        short money = (short) user.getMoney();
        short range = (short) (money - (columns - 2)); //e.g. if money=500, range = 500 - (columns-2) == 497
        short maxMMP = 1, maxMMP1, maxMMP2, maxMMP3, maxMMP4;


        float[] sortedMMPs = new float[mmpList.length];
        for (int i =0; i < mmpList.length; i++) {
            sortedMMPs[i] = mmpList[i];
        }
        Arrays.sort(sortedMMPs);
        short maxrange = (short)(money - (sortedMMPs[sortedMMPs.length-1]) - columns-2);

        ArrayList<float[]> comboTable = new ArrayList<float[]>();

        if (totalDebts == 4) {

            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];
            short mmp3 = (short) mmpList[2];
            short mmp4 = (short) mmpList[3];

            //this establishes the height of the range of possible mmps ("maxMMP")
            float[] testArray = new float[12];
            ArrayList<float[]> tempHolder = generateCombosHelper((byte) (0), mmp1, (byte) (1), mmp2, user);
            try {
                testArray[0] = tempHolder.get(0)[0];
                testArray[1] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (0), mmp1, (byte) (2), mmp3, user);
                testArray[2] = tempHolder.get(0)[0];
                testArray[3] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (0), mmp1, (byte) (3), mmp4, user);
                testArray[4] = tempHolder.get(0)[0];
                testArray[5] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (1), mmp2, (byte) (2), mmp3, user);
                testArray[6] = tempHolder.get(0)[0];
                testArray[7] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (1), mmp2, (byte) (3), mmp4, user);
                testArray[8] = tempHolder.get(0)[0];
                testArray[9] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (2), mmp3, (byte) (3), mmp4, user);
                testArray[10] = tempHolder.get(0)[0];
                testArray[11] = tempHolder.get(0)[1];

//                Arrays.sort(testArray); //PAY ATTENTION TO THIS FOR SETTING INDIVIDUAL MAXMMPS
            } catch (IndexOutOfBoundsException e) {
                testArray[0] = 0;
            }

            maxMMP1 = (short)testArray[0];
            if (maxMMP1 < testArray[2])
                maxMMP1 = (short)testArray[2];
            if (maxMMP1 < testArray[4])
                maxMMP1 = (short)testArray[4];

            maxMMP2 = (short)testArray[1];
            if (maxMMP2 < testArray[6])
                maxMMP2 = (short)testArray[6];
            if (maxMMP2 < testArray[8])
                maxMMP2 = (short)testArray[8];

            maxMMP3 = (short)testArray[3];
            if (maxMMP3 < testArray[7])
                maxMMP3 = (short)testArray[7];
            if (maxMMP3 < testArray[10])
                maxMMP3 = (short)testArray[10];

            maxMMP4 = (short)testArray[5];
            if (maxMMP4 < testArray[9])
                maxMMP4 = (short)testArray[9];
            if (maxMMP4 < testArray[11])
                maxMMP4 = (short)testArray[11];



            Arrays.sort(testArray);
            maxMMP = (short)testArray[testArray.length-1];

            float sampleMoney = money - (mmp1+mmp2+mmp3+mmp4);
            sampleMoney = sampleMoney/4;
            //each mmp has an even share of the remaining sample money
            float[] sampleArray = {mmp1+sampleMoney,mmp2+sampleMoney,mmp3+sampleMoney,mmp4+sampleMoney};
            float samplePeriods = calculateComboPeriods(sampleArray,user);

            range = maxMMP;
            //arrays to contain all possible values in range
//            short[] mmp1Array = new short[range];
            short[] mmp1Array = new short[maxMMP1], mmp2Array = new short[maxMMP2], mmp3Array = new short[maxMMP3], mmp4Array = new short[maxMMP4];

//            for (short i = 0; ++i <= range;) {
//                //populate each array with all possible values in range (e.g. 5-297
//                if ((mmp1 + i) <= range)
//                    mmp1Array[i] = (short) (mmp1 + i); ...

            for (short i = 0; i < mmp1Array.length; i++)
                mmp1Array[i] = (short) (mmp1 +i);
            for (short j = 0; j < mmp2Array.length; j++)
                mmp2Array[j] = (short) (mmp2 +j);
            for (short k = 0; k < mmp3Array.length; k++)
                mmp3Array[k] = (short) (mmp3 +k);
            for (short l = 0; l < mmp4Array.length; l++)
                mmp4Array[l] = (short) (mmp4 + l);

            long epsilon =0, omicron=0, eta=0, lambda=0;

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            short miniRange1 = (short) (mmp1Array.length), miniRange2 = (short) (mmp2Array.length), miniRange3 = (short) (mmp3Array.length), miniRange4 = (short) (mmp4Array.length);
            float[] row, testArray1, testArray2, testArray3;

            short mmp1Entry, mmp2Entry, mmp3Entry, mmp4Entry;
            for (short z = 0; z < miniRange1; z++) {
                row = new float[columns];
                mmp1Entry = mmp1Array[z];
                //column2 of comboTable
                testArray1 = new float[] {mmp1Entry, sampleArray[1], sampleArray[2], sampleArray[3]}; //added ++ ?
                if ((samplePeriods > (calculateComboPeriods(testArray1, user))) && mmp1Entry <= maxMMP1) {
                    for (short y = 0; y < miniRange2; y++) {
                        epsilon++;
                        row = new float[columns];
                        mmp2Entry = mmp2Array[y];
                        //column3 of comboTable
                        testArray2 = new float[] {sampleArray[0], mmp2Entry, sampleArray[2], sampleArray[3]};
                        if ((samplePeriods > (calculateComboPeriods(testArray2, user))) && mmp2Entry <= maxMMP2) {
                            for (short x = 0; x < miniRange3; x++) {
                                omicron++;
                                row = new float[columns];
                                mmp3Entry = mmp3Array[x];
                                //column 4 of comboTable
                                testArray3 = new float[]{sampleArray[0], sampleArray[1], mmp3Entry, sampleArray[3]};
                                if ((samplePeriods > (calculateComboPeriods(testArray3, user)))  && mmp3Entry <= maxMMP3) {
                                    for (short w = 0; w < miniRange4; w++) {
                                        eta++;
                                        row = new float[columns];
                                        mmp4Entry = mmp4Array[w];

                                        row[0] = mmp1Entry;
                                        row[1] = mmp2Entry;
                                        row[2] = mmp3Entry;
                                        row[3] = mmp4Entry;
                                        float rowSum = row[0] + row[1] + row[2] + row[3];

                                        //filter#1: sum of mmp's must == budget
                                        if (rowSum == money) {
                                            lambda++;

                                            //calculate totalIntr and set as row[3]
                                            if (comboTable.size() > 1) {
                                                if ((row[4] = calculateComboPeriods(row, user)) <= comboTable.get(0)[4]) {
                                                    comboTable.add(row);
                                                    sortByPeriods(comboTable, columns);
                                                    comboTable.remove(2);
                                                }
                                            } else {
                                                row[4] = calculateComboPeriods(row, user);
                                                comboTable.add(row);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } //close combo for loop
            System.out.println("epsilon="+epsilon);
            System.out.println("omicron="+omicron);
            System.out.println("eta="+eta);
            System.out.println("lambda="+lambda);

        } else if (totalDebts == 3) {

            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];
            short mmp3 = (short) mmpList[2];

            //Finds the ceiling for mmp by matching up each debt individually;
            //when finding the opt. combo for 3, we know each mmp will be less than
            //when there are only 2 to optimize for
            float[] testArray = new float[6];
            ArrayList<float[]> tempHolder = generateCombosHelper((byte) (0), mmp1, (byte) (1), mmp2, user);
            try {
                testArray[0] = tempHolder.get(0)[0];
                testArray[1] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (0), mmp1, (byte) (2), mmp3, user);
                testArray[2] = tempHolder.get(0)[0];
                testArray[3] = tempHolder.get(0)[1];
                tempHolder = generateCombosHelper((byte) (1), mmp2, (byte) (2), mmp3, user);
                testArray[4] = tempHolder.get(0)[0];
                testArray[5] = tempHolder.get(0)[1];

            } catch (IndexOutOfBoundsException e) {
                testArray[0] = 0;
            }
            //above tempHolder has rows, each index is the opt payment for that debt


            maxMMP1 = (short)testArray[0];
            if (maxMMP1 < testArray[2])
                maxMMP1 = (short)testArray[2];
            maxMMP2 = (short)testArray[1];
            if (maxMMP2 < testArray[4])
                maxMMP2 = (short)testArray[4];
            maxMMP3 = (short)testArray[3];
            if (maxMMP3 < testArray[5])
                maxMMP3 = (short)testArray[5];
//            Arrays.sort(testArray);
//            maxMMP = (short)testArray[testArray.length-1];
            System.out.println("maxmmp1: "+maxMMP1);
            System.out.println("maxmmp2: "+maxMMP2);
            System.out.println("maxmmp3: "+maxMMP3);


//            System.out.println("****test array****");
//            System.out.println(Arrays.toString(testArray));
//            System.out.println("\n");


            float debtSum = (float) (user.getDebts().get(0).getPrincipal()+user.getDebts().get(1).getPrincipal()+user.getDebts().get(2).getPrincipal());
            float d1share = (float)(user.getDebts().get(0).getPrincipal()) / debtSum;
            float d2share = (float)(user.getDebts().get(1).getPrincipal()) / debtSum;
            float d3share = (float)(user.getDebts().get(2).getPrincipal()) / debtSum;
            float moneyLessMmps = money - (mmp1+mmp2+mmp3);
            float sampleMoney1 = moneyLessMmps/3; //money after accounting for the mmp for each
            float d1shareAmt = moneyLessMmps*d1share;
            float d2shareAmt = moneyLessMmps*d2share;
            float d3shareAmt = moneyLessMmps*d3share;

            float[] sampleArray = {mmp1+sampleMoney1,mmp2+sampleMoney1,mmp3+sampleMoney1}; //"average" split
            float[] sample2Array = {mmp1+d1shareAmt,mmp2+d2shareAmt,mmp3+d3shareAmt}; //amount "average" split

            float samplePeriods = calculateComboPeriods(sampleArray,user), sample2Periods;
            System.out.println("before: "+samplePeriods);
            if ((sample2Periods=calculateComboPeriods(sample2Array, user)) < samplePeriods)
                samplePeriods=sample2Periods;
            System.out.println("after: "+samplePeriods);

//            range = maxMMP;

            //arrays to contain all possible values in range
            short[] mmp1Array = new short[maxMMP1], mmp2Array = new short[maxMMP2], mmp3Array = new short[maxMMP3];

//            for (short i = 0; i <= range; i++) {
//                //populate each array with all possible values in range
//                if ((mmp1 + i) <= range)
//                    mmp1Array[i] = (short) (mmp1 + i); ...


            for (short i = 0; i < mmp1Array.length;i++)
                mmp1Array[i] = (short) (mmp1 +i);
            for (short j = 0; j < mmp2Array.length;j++)
                mmp2Array[j] = (short) (mmp2 +j);
            for (short k = 0; k < mmp3Array.length;k++)
                mmp3Array[k] = (short) (mmp3 +k);

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            int alpha=0, beta=0, gamma=0, delta=0;
            short miniRange1 = (short) (mmp1Array.length), miniRange2 = (short) (mmp2Array.length), miniRange3 = (short) (mmp3Array.length);
            short mmp1Entry, mmp2Entry, mmp3Entry;
            float[] testArray1, testArray2, row;
            float rowSum;
            for (short z = 0; ++z < miniRange1;) {
                row = new float[columns];
                mmp1Entry = mmp1Array[z];

                //column2 of comboTable
                testArray1 = new float[] {mmp1Entry++, sampleArray[1], sampleArray[2]}; //Added ++ for speed?
                if ((samplePeriods > (calculateComboPeriods(testArray1, user))) && (mmp1Entry < maxMMP1)) {
                    for (short y = 0; ++y < miniRange2; ) {
                        alpha++;
                        row = new float[columns];
                        mmp2Entry = mmp2Array[y];

                        //column3 of comboTable
                        testArray2 = new float[] {sampleArray[0], mmp2Entry, sampleArray[2]};
                        if (samplePeriods > (calculateComboPeriods(testArray2, user)) && (mmp2Entry < maxMMP2)) {
                            for (short x = 0; ++x < miniRange3; ) {
                                beta++;
                                row = new float[columns];
                                mmp3Entry = mmp3Array[x];

                                row[0] = mmp1Entry;
                                row[1] = mmp2Entry;
                                row[2] = mmp3Entry;
                                rowSum = row[0] + row[1] + row[2];

                                //filter#1: sum of mmp's must == budget
                                if (rowSum==money ) {

                                    gamma++;
                                    //this is my major timesuck
                                    //calculate totalIntr and set as row[3]
                                    if (comboTable.size() > 1) {
                                        if ((row[3] = calculateComboPeriods(row, user)) <= comboTable.get(0)[3]) {
                                            delta++;
                                            comboTable.add(row);
                                            sortByPeriods(comboTable, columns);
                                            comboTable.remove(2);
                                        }
                                    } else {
                                        row[3] = calculateComboPeriods(row, user);
                                        comboTable.add(row);
                                    }
                                }
                            }
                        }
                    }
                } //close combo for loop
            }
            System.out.println("alpha="+alpha);
            System.out.println("beta="+beta);
            System.out.println("gamma="+gamma);
            System.out.println("delta="+delta);
//            System.out.println("delta="+delta);

        } else if (totalDebts == 2) {

            //starting values of each variable = mmp for that debt
            short mmp1 = (short) mmpList[0];
            short mmp2 = (short) mmpList[1];

            //arrays to contain all possible values in range
            short[] mmp1Array = new short[range];
            short[] mmp2Array = new short[range];

            for (short i = 0; i < range; i++) {
                //populate each array with all possible values in range
                if ((mmp1 + i) <= range)
                    mmp1Array[i] = (short) (mmp1 + i);
                if ((mmp2 + i) <= range)
                    mmp2Array[i] = (short) (mmp2 + i);
            }

            //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
            for (short z = 0; ++z <= (range - mmp1);) {
                float[] row = new float[columns];
                short mmp1Entry = mmp1Array[z];
                //column2 of comboTable
                for (short y = 0; ++y <= (range - mmp2);) {
                    row = new float[columns];
                    short mmp2Entry = mmp2Array[y];

//                    float totalIntr = 0;

                    row[0] = mmp1Entry;
                    row[1] = mmp2Entry;
                    float rowSum = row[0] + row[1];

                    //filter#1: sum of mmp's must == budget
                    if (rowSum == money) {

                        //calculate totalIntr and set as row[3]
                        if (comboTable.size() > 1) {
                            if ((row[2] = calculateComboPeriods(row, user)) <= comboTable.get(0)[2]) {
                                comboTable.add(row);
                                sortByPeriods(comboTable, columns);
                                comboTable.remove(2);
                            }
                        } else {
                            row[2] = calculateComboPeriods(row, user);
                            comboTable.add(row);
                        }
                    }
                }
            } //close combo for loop

        } else {
            System.out.println("Debt quantity out of range. Please enter between 2 and 4 debts.");
        }

        try {
            float[] row = comboTable.get(0);
            row[(row.length-1)] = calculateComboIntr(row, user);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: insufficient monthly budget.");
        }
//        row[(row.length-1)] = calculateComboIntr(row, user);
//        System.out.println(row);


        double nanoToSec2 = Math.pow(10, -9);
        long endTime2 = System.nanoTime();
        long totalTime2 = endTime2 - startTime2;
        double totalDubTime2 = totalTime2 * nanoToSec2;
        System.out.println(totalDubTime2);

        return comboTable;
    } //close method

    public static float[] calculateMMPList(User user) {
        int totalDebts = user.getDebts().size();

        float[] mmpList = new float[totalDebts];
//        short[] periodTracker = new short[totalDebts];

        //Repayment Loop#1: determine mmp for each debt
        for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {
            float[] totalIntrTracker = new float[totalDebts];
            short period = 0;
            float totalIntr = 0;
            //accruedBal starts = principal then accrues interest during repayment
            float lastPrincipal = (float) user.debts.get(debtNumber).getPrincipal();
            float accruedBal = lastPrincipal;
            //interestRate of the current debt in the loop
            float rate = (float) user.debts.get(debtNumber).getInterestRate();
            float r = (rate / CF);
            //get minimum monthly payment amount, roundup to nearest int and add 0
            float mmp = (float) (Math.rint(lastPrincipal * rate)) + 0;

            //declare a new array to log all payment transactions against debt
            ArrayList<Float> thisTransactionArray = new ArrayList<Float>();

            //Reset the while loop on the next debtNumber
            if (accruedBal <= 0)
                debtNumber++;

            System.out.println("*******************");
            System.out.println("Begin Debt#" + (debtNumber + 1) + " repayment of: " +
                    user.debts.get(debtNumber).getDebtName() + "\nPrincipal:$" + user.debts.get(debtNumber).getPrincipal() +
                    "\nInterest:" + (user.debts.get(debtNumber).getInterestRate() * 100) + "%");
            System.out.println("*******************");

            //Loop to get mmp i.e. minimum monthly payment to pay down debt
            while (accruedBal > 0) {
                //case 1: first period of repayment
                if (period == 0)
                {
                    float roundIntr;
                    roundIntr = (float) (Math.round((accruedBal * r) * 100.0) / 100.0);
                    accruedBal += (roundIntr - mmp);

                    thisTransactionArray.add(accruedBal);
                    totalIntr += roundIntr;
                    ++period;

                    //case 2: any period after the first
                } else {

                    //accrue interest over last period's balance, then deduct mmp
                    float roundIntr;
                    roundIntr = (float) (Math.round(((thisTransactionArray.get(period - 1)) * r) * 100.0) / 100.0);
                    accruedBal += (roundIntr - mmp);

                    //log new balance in current period
                    thisTransactionArray.add(accruedBal);
                    totalIntr += roundIntr;

                    if (period >= 1) {
                        //If the balance of the current period >= the last, increment mmp $0.01
                        if (thisTransactionArray.get(period) >= thisTransactionArray.get(period - 1))
                            mmp += 1;
                    }
//                    //end the while loop if debt paid off, log totalIntr in tracker
                    if (accruedBal <= 0) {
//                        System.out.println("Debt#" + (debtNumber + 1) + " Repayment Complete: \n--mmp:$" + mmp + "\n--periods:" + period);
//                        periodTracker[debtNumber] = period;
                        mmpList[debtNumber] = mmp;
                    } else {
                        period++;
                    }
                }
            }
        }
        return mmpList;
    } //close method

    public static void getDebtsFromUser(User user) {
        String debtName, userInputStr;
        float principal;
        double rate;
        byte i = 0;
        while (i<3) {
            System.out.print("\nAdding debt#" + (i+1) + "...\n");

            System.out.print("What is this debt called? (use_under_scores)");
            userInputStr = inputStream.next();
            debtName = userInputStr;
//            System.out.println("\nEntered: "+ debtName);

            System.out.print("\nWhat is the principal / current balance of this debt? (e.g. if $5,000, enter: 5000)");
            userInputStr = inputStream.next();
            principal = Float.parseFloat(userInputStr);
//            System.out.println("\nEntered: "+ principal);

            System.out.print("What is the current interest rate of this debt? (e.g. if 5%, enter: 0.05)");
            userInputStr = inputStream.next();
            rate = (Double.parseDouble(userInputStr)*100)/100;
            System.out.println("\n");

            user.addDebt(debtName, principal, rate);
            System.out.println("Added debt:\n"+debtName+"\n$"+principal+"\n"+(rate * 100)+"%");

            if (i < 2)
            { //i.e. if 2 or fewer debts have been entered
                System.out.print("\nWould you like to add another debt? (y/n)");
                userInputStr = inputStream.next();

                if (Character.toUpperCase((userInputStr).charAt(0)) == 'Y' )
                    i++;
                else
                    i+=2;
            } else {
                i+=2;
            }
        }
        System.out.println("All debts have been added.");
    } //close method

    public static User createUser() {
        String userName, userInputStr;
        float money;
        System.out.print("Please enter your first and last name: ");
        userName = inputStream.nextLine();

        System.out.print("Please enter your monthly budget dedicated to paying off your total debt. " +
                "(e.g. if $1000, enter: 1000\namount: ");
        money = Float.parseFloat(inputStream.next());

        User user = new User(userName, money);
        System.out.println("New user created:\n"+userName+"\nmonthly budget: $"+money+"\n");
        return user;
    }

    public static void presentData(User user, float[] row) {
        int[] periodHolder = new int[row.length-1];
        int quickestRepay = 0;

        for (int i = 0; i < periodHolder.length; i++) { //-1 b/c last index is for interest
//            System.out.println("now parsing debt#"+(i+1)+": "+user.debts.get(i).getDebtName());

            periodHolder[i] = getPeriods2(user, row, i); //periodHolder[debtNumber]... user, row, i = debtNumber
            System.out.println("Debt#" + (Integer.toString(i + 1)) + " monthly payment: $" + row[i]);
//            System.out.println("Paid off in "+totalPeriods+" months.");

            if ((i == (periodHolder.length - 1))) { //only 2 debts
                System.out.println("Total interest paid: $" + row[(row.length - 1)]); //interest index
//                System.out.println("You will have paid off debt#"+(Integer.toString(quickestRepay) + 1)+": "+user.debts.get(i).getDebtName());
//                System.out.println("in "+periodHolder[0]+" months. Re-run program at that time.");

            }
//            else if (row.length == 3 && (i == (row.length - 2))) {//last mmp index (2nd to last of row indices)
////                Arrays.sort(periodHolder);
//                System.out.println("Total interest paid: $" + row[(row.length - 1)]); //interest index
//                System.out.println("You will have paid off debt#"+(Integer.toString(quickestRepay) + 1)+": "+user.debts.get(i).getDebtName());
//                System.out.println("in "+periodHolder[0]+" months. Re-run program at that time.");
//            }
        }
        System.out.println(Arrays.toString(periodHolder));

        int fastestPayoff = -1, k = 0;
        while (k < periodHolder.length && fastestPayoff < 0) {
            switch (k) {
                case 0:
                    if (periodHolder[k] <= periodHolder[1] && periodHolder[k] <= periodHolder[2])
                        fastestPayoff = k;
                    break;
                case 1:
                    if (periodHolder[k] <= periodHolder[0] && periodHolder[k] <= periodHolder[2])
                        fastestPayoff = k;
                    break;
                case 2:
                    if (periodHolder[k] <= periodHolder[0] && periodHolder[k] <= periodHolder[1])
                        fastestPayoff = k;
                    break;
                    default:
                        k++;
                        break;
            }
            k++;
        }
        System.out.println("You will have paid off debt#"+(fastestPayoff + 1)+": "+user.debts.get(fastestPayoff).getDebtName());
        System.out.println("in "+periodHolder[fastestPayoff]+" months. Re-run program at that time.");
    }

    public static int getPeriods(User user, float[] row, int debtNumber) {

        float accruedBal = (float) user.debts.get(debtNumber).getPrincipal();
        short period = 0;
        int finalPeriod = 0;
        float mmp = row[debtNumber], totalIntr = 0.f, rate = (float) user.debts.get(debtNumber).getInterestRate(), r = (rate / CF);
        ArrayList<Float> thisTransactionArray = new ArrayList<>();

        //Loop until zero bal
        while (accruedBal > 0) {
            //case 1: first period of repayment
            if (period == 0)
            {
                float roundIntr;
                roundIntr = (float) (Math.round((accruedBal * r) * 100.0) / 100.0);
                accruedBal += (roundIntr - mmp);
                thisTransactionArray.add(accruedBal);
                ++period;

                //case 2: any period after the first
            } else {

                //accrue interest over last period's balance, then deduct mmp
                float roundIntr;
                roundIntr = (float) (Math.round(((thisTransactionArray.get(period - 1)) * r) * 100.0) / 100.0);
                accruedBal += (roundIntr - mmp);
                thisTransactionArray.add(accruedBal); //log new balance in current period

                if (accruedBal <= 0)
                    finalPeriod = period;
                else
                    period++;
            }
        }
        return finalPeriod;
    }

    public static int getPeriods2(User user, float[] row, int debtNumber) {

        float princip = (float) user.debts.get(debtNumber).getPrincipal();

        //interestRate of the current debt in the loop
        float rate = (float) user.debts.get(debtNumber).getInterestRate();
        float r = 1+(rate/ CF); //e.g. 1.012


        //grab mmp from corresponding index in input array
        float mmp = row[debtNumber];

        //ln(princip/mmp * (r-1) ) / (ln(r^2) - ln(r) ) = x-intercept of balance function, i.e. when y=0
        double periodsToZeroBal = ((Math.log((mmp/(r-1))/(-1*(princip-(mmp/(r-1)))))) / (((Math.log(Math.pow(r,2)))-(Math.log(r)))));
        periodsToZeroBal = Math.ceil(periodsToZeroBal);
        return ((int)periodsToZeroBal);
    }
}




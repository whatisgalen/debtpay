package com.galen;

import java.util.*;

public class Main {

//    public static User galen1 = new User("galen mancino");
    public static Scanner inputStream = new Scanner(System.in);

    //compounding frequency per year
    final static short COMPOUND_FREQUENCY = 12; //should be 12 for monthly || 1 for annually; 365 not ready


    public static void main(String[] args) {

//        User user = createUser();
        User user = new User("galen",1000.f);
//        getDebtsFromUser(user);

        user.addDebt("Student Debt", 8000, 0.0584d);
        user.addDebt("Cheap Car loan", 7500, 0.09d);
        user.addDebt("Misc", 15000, 0.063d);
//        user.addDebt("DebtFour", 9000, 0.04d);
//        user.addDebt("Mortgage",300000,0.045d); //this will have to wait for the memory problem
//        System.out.println("debts are: " + galen.debts);
        user.setMoney(1000.0f);

        //Performance notes
        //@ 4debts, $1000 takes

        byte totalDebts = (byte) user.debts.size();
        float[] mmpList = calculateMMPList(user), data;
        long startTime = System.nanoTime();
        double nanoToSec = Math.pow(10, -9);

        ArrayList<float[]> comboTable = generateCombos(totalDebts,mmpList,user);

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

    //Callable method that takes 3 mmp's as inputs and returns a float of totalIntr
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
            float r = (rate / COMPOUND_FREQUENCY);

            //grab mmp from corresponding index in input array
            float mmp = row[debtNumber];

            //declare an array to log all payment transactions against debt
            ArrayList<Float> thisTransactionArray = new ArrayList<Float>();

            //loop to get accruedIntr for this debt
            while (accruedBal > 0) {

                float roundIntr;
                float miniIntr1;
                float miniIntr2;

                //case 1: first period of repayment
                if (period == 0) {

                    //save some time
                    miniIntr1 = accruedBal * r;
                    miniIntr2 = (Math.round(miniIntr1 * 100));
                    roundIntr = (miniIntr2 / 100);
                    accruedBal += (roundIntr - mmp);

                    thisTransactionArray.add(accruedBal);
                    accruedIntr += roundIntr;
                    period++;

                    //case 2: any period after the first
                } else {

                    //accrue interest over last period's balance, then deduct mmp
                    float lastTransaction = thisTransactionArray.get(period - 1);
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
        return totalIntr;
    }

    public static void sortByTotalIntr(ArrayList<float[]> comboTable, byte columns) {

        Collections.sort(comboTable, (a, b) -> Float.compare(a[columns-1], b[columns-1]));
    }

//    public static void sortByFinalPeriod(int[] periodHolder, byte i) {
//
//        Arrays.sort(periodHolder, (a, b) -> Arrays.compare(a[i], b[i-1]));
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
//            System.out.println(Arrays.toString(mmpList));
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

//            comboTable = new ArrayList<float[]>();

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

                                        float totalIntr = 0;
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
//            System.out.println(Arrays.toString(mmpList));
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

                                float totalIntr = 0;

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
//            System.out.println(Arrays.toString(mmpList));

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

                    float totalIntr = 0;

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
            float r = (rate / COMPOUND_FREQUENCY);
            //get minimum monthly payment amount, roundup to nearest int and add 0
            float mmp = (float) (Math.rint(lastPrincipal * r)) + 0;

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

            periodHolder[i] = getPeriods(user, row, i); //periodHolder[debtNumber]... user, row, i = debtNumber
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
        float mmp = row[debtNumber], totalIntr = 0.f, rate = (float) user.debts.get(debtNumber).getInterestRate(), r = (rate / COMPOUND_FREQUENCY);
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
}




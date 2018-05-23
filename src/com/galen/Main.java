package com.galen;

import java.util.*;

public class Main {

    public static void main(String[] args) {


        User galen = new User("galen mancino");

        galen.addDebt("Student Debt",5000,0.023d);
        galen.addDebt("Car Loan",1700,0.057d);
        galen.addDebt("Credit",2500,0.7d);
//        galen.addDebt("Other Debt",1000,0.1d); //this will have to wait for the memory problem
//        System.out.println("debts are: " + galen.debts);
        galen.setMoney(1200.00d); // max=436 for comboTable (tops at 70m combinations)

        byte totalDebts = (byte) galen.debts.size();
        //Object currentDebt = galen.debts.get(debtNumber);
        float money = (float) galen.getMoney();
        //compounding frequency per year
        short compounded = 12;

        //arrayList to capture mmps for each debt
        float[] mmpList = new float[totalDebts];
        short[] periodTracker = new short[totalDebts];

//        ******************************************************
        //Repayment Loop#1: determine mmp for each debt
        for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {
            float[] totalIntrTracker = new float[totalDebts];
            short period = 0;
            float totalIntr = 0;
            //accruedBal starts = principal then accrues interest during repayment
            float lastPrincipal = (float) galen.debts.get(debtNumber).getPrincipal();
            float accruedBal = lastPrincipal;
            //interestRate of the current debt in the loop
            float rate = (float) galen.debts.get(debtNumber).getInterestRate();
            float r = (rate/compounded);
            //get minimum monthly payment amount, roundup to nearest int
            float mmp = (float) (Math.rint(lastPrincipal * r)) + 0;

            //declare a new array to log all payment transactions against debt
            ArrayList<Float> thisTransactionArray = new ArrayList<Float>();

            //Reset the while loop on the next debtNumber
            if (accruedBal<=0) {
                debtNumber++;
//                System.out.println("debt# is now:"+debtNumber);
            }
            System.out.println("Begin Debt#"+(debtNumber+1)+" repayment of: "+
            galen.debts.get(debtNumber).getDebtName()+"\nPrincipal:$"+galen.debts.get(debtNumber).getPrincipal()+
            "\nInterest:"+(galen.debts.get(debtNumber).getInterestRate()*100)+"%");

            //Loop to get mmp i.e. minimum monthly payment to pay down debt
            while (accruedBal > 0) {
                //case 1: first period of repayment
                if (period == 0) {
                    float roundIntr;
                    roundIntr = (float) (Math.round((accruedBal*r)*100.0) / 100.0);
                    accruedBal = accruedBal + roundIntr - mmp;

//                    double roundBal;
//                    roundBal = (accruedBal*(1+r))-mmp;
//                    accruedBal = Math.round(roundBal * 100.0) / 100.0;
                    thisTransactionArray.add(accruedBal);
                    totalIntr += roundIntr;
                    ++period;

                //case 2: any period after the first
                } else {

                    //accrue interest over last period's balance, then deduct mmp
                    float roundIntr;
                    roundIntr = (float) (Math.round(((thisTransactionArray.get(period-1))*r)*100.0) / 100.0);
                    accruedBal = accruedBal + roundIntr - mmp;

                    //log new balance in current period
                    thisTransactionArray.add(accruedBal);
                    totalIntr += roundIntr;


                    if (period >= 1) {
                        //If the balance of the current period >= the last, increment mmp $0.01
                        if (thisTransactionArray.get(period) >= thisTransactionArray.get(period - 1)) {
                            mmp += 1;
                            System.out.println("ATTN: mmp raised to:$"+mmp);
                        }
                    }
                    //end the while loop if debt paid off, log totalIntr in tracker
                    if (accruedBal <= 0) {
                        System.out.println("Debt#"+(debtNumber+1)+" Repayment Complete: \n--mmp:$"+mmp+"\n--periods:"+period);
//                        periodTracker[debtNumber] = period;
                        mmpList[debtNumber] = mmp;
                    } else {
                        period++;
                    }
                }
            }
        }

//        System.out.println(Arrays.toString(periodTracker));


//***************************************************************************
//        Find mmp combinations, both Total and Valid
        short n = (short) totalDebts; //=3
        short shortMoney = (short) money;
        short range = (short) (shortMoney - (n-1));

        //starting values of each variable = mmp for that debt
        short d1 = (short) mmpList[0];
        short d2 = (short) mmpList[1];
        short d3 = (short) mmpList[2];
        System.out.println(Arrays.toString(mmpList));

        //arrays to contain all possible values in range
        short[] dd1 = new short[range];
        short[] dd2 = new short[range];
        short[] dd3 = new short[range];

        for (short k=0;k<range;k++) {
            //populate each array with all possible values in range
            //if blocks to cap array values at 414
            if ((d1+k) <= range) {
                dd1[k] = (short) (d1+k);
            }
            if ((d2+k) <= range) {
                dd2[k] = (short) (d2+k);
            }
            if ((d3+k) <= range) {
                dd3[k] = (short) (d3+k);
            }
        }

//        ArrayList<short[]> comboTable = new ArrayList<short[]>();

        ArrayList<short[]> comboTable = new ArrayList<short[]>();

        //column1 of comboTable; limit = range-mmpList[i] so as not to produce 0's
        for (short x=0;x<=(range-d1);x++) {
            short[] row = new short[n];
            short ddigit1 = dd1[x];
            //column2 of comboTable
            for (short y=0;y<=(range-d2);y++) {
                row = new short[n];
                short ddigit2 = dd2[y];
                //column3 of comboTable
                for (short z=0;z<=(range-d3);z++) {
                    row = new short[n];
                    short ddigit3 = dd3[z];
                    row[0] = ddigit1;
                    row[1] = ddigit2;
                    row[2] = ddigit3;

                    if (row[0]+row[1]+row[2] == shortMoney) { //if the sum = the budget, add to comboTable
                        comboTable.add(row);
                    }
//                    comboTable.add(row);
                }
            }
        }
        System.out.println("Total Combinations: "+comboTable.size());
//        for (int i=250;i<450;i++) {
//            System.out.println(Arrays.toString(comboTable.get(i)));
//        }

//        ArrayList<double[]> validCombos = new ArrayList<double[]>();
        Combo combo = new Combo();
        ArrayList<float[]> validCombos = combo.getValidCombos();

        for (int i = 0;i < comboTable.size();i++) {
            short[] row = comboTable.get(i);
            if (row[0]+row[1]+row[2] == shortMoney) {
                //then cast each row[] value to a double & add to an entry[]
                float[] entry = new float[4];
                entry[0] = (float) row[0];
                entry[1] = (float) row[1];
                entry[2] = (float) row[2];
                entry[3] = 0;

                validCombos.add(entry); //replace this .add() with a linkedList method .addInOrder()
            }
        }
        System.out.println("Valid Combinations: "+validCombos.size());

//        ****************************************************************


        //Repayment Loop#2: get total interest for each valid combo

        for(int entryNumber = 0; entryNumber < validCombos.size(); entryNumber++) {

            //reset totalIntrTracker
            float[] totalIntrTracker = new float[totalDebts];

            for (byte debtNumber = 0; debtNumber < totalDebts; debtNumber++) {

                //reset period and totalIntr for this debt
                short period = 0;
                float totalIntr = 0;

                //accruedBal starts = principal then accrues interest during repayment
                float lastPrincipal = (float) galen.debts.get(debtNumber).getPrincipal();
                float accruedBal = lastPrincipal;

                //interestRate of the current debt in the loop
                float rate = (float) galen.debts.get(debtNumber).getInterestRate();
                float r = (rate / compounded);

                //access the mmp from the entry in validCombos
                float mmp = validCombos.get(entryNumber)[debtNumber];

                //declare an array to log all payment transactions against debt
                ArrayList<Float> thisTransactionArray = new ArrayList<Float>();

                //Reset the while loop on the next debtNumber
                if (accruedBal <= 0) {
                    debtNumber++;
                }

                //loop to get totalIntr for this debt
                while (accruedBal > 0) {

                    //case 1: first period of repayment
                    if (period == 0) {
                        float roundIntr;
                        roundIntr = (float) (Math.round((accruedBal * r) * 100.0) / 100.0);
                        accruedBal = accruedBal + roundIntr - mmp;

                        thisTransactionArray.add(accruedBal);
                        totalIntr += roundIntr;
                        ++period;

                        //case 2: any period after the first
                    } else {

                        //accrue interest over last period's balance, then deduct mmp
                        float roundIntr;
                        roundIntr = (float) (Math.round(((thisTransactionArray.get(period - 1)) * r) * 100.0) / 100.0);
                        accruedBal = accruedBal + roundIntr - mmp;

                        //log new balance in current period
                        thisTransactionArray.add(accruedBal);
                        totalIntr += roundIntr;

                        //end the while loop if debt paid off, log totalIntr in tracker
                        if (accruedBal <= 0) {
                            totalIntrTracker[debtNumber] = totalIntr;
//                            periodTracker[debtNumber] = period;
                        } else {
                            period++;
                        }
                    }

                } //while looping through thisTransactionArray (period++)

            } //for looping through debts (debtNumber++)

            float entryIntr = (totalIntrTracker[0] + totalIntrTracker[1] + totalIntrTracker[2]);
            validCombos.get(entryNumber)[3] = entryIntr;

        } //for looping through validCombos (entryNumber++)
        System.out.println("Total Interest calculated for each entry in validCombos");

        //        System.out.println("comboTable for loop");
//        for (int i=400;i<420;i++) {
//            System.out.println(Arrays.toString(validCombos.get(i)));
//        }


        //***************************************************************************
        //Sort validCombos least->greatest on entry[3] which is the totalIntr for that entry
        Collections.sort(validCombos, new Comparator<float[]>() {
            @Override
            public int compare(float[] a, float[] b) {
                return Float.compare(a[3], b[3]);
            }
        });
//        //enhanced for loop
//        for (double[] i : validCombos) {
//            System.out.println(Arrays.toString(i));
//        }
        for (byte i=0;i<3;i++) {
            System.out.println(Arrays.toString(validCombos.get(i)));
        }
    }
    //^^^Main method closing bracket

}




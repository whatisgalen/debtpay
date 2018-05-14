package com.galen;

import java.util.ArrayList;

public class Debt {
    private String debtName;
    private double principal;
    private double interestRate;

    //variables for calculation
    private double accruedInterest;
    public double totalInterest;

    private ArrayList<Double> transactions;

    public Debt(String debtName, double principal, double interestRate) {
        this.debtName = debtName;
        this.principal = principal;
        this.interestRate = interestRate;
        this.accruedInterest = accruedInterest;
        this.totalInterest = totalInterest;
        this.transactions = transactions;
    }


    public String getDebtName() {
        return debtName;
    }

    public double getPrincipal() {
        return principal;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getAccruedInterest() {
        return accruedInterest;
    }

    public double getTotalInterest() {
        return totalInterest;
    }

    public ArrayList<Double> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return this.getDebtName()+", principal: "+ this.getPrincipal() +", interestRate: "+ (this.getInterestRate()*100)+"%";
    }


}

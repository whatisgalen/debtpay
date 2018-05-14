package com.galen;

import java.util.ArrayList;

public class User {

    public String userName;
    public ArrayList<Debt> debts;
    public double money;

    //user object needs a numerical user_id and an array called "debts" of object class: Debt
    public User(String userName) {
        this.userName = userName;
        this.debts = new ArrayList<Debt>();
        this.money = 0;
    }

    public void addDebt(String debtName, double principal, double interestRate) {
        this.debts.add(new Debt(debtName, principal, interestRate));
    }



    public String getUserName() {
        return this.userName;
    }

    public void setUserName() {
        this.userName = userName;
    }

    public ArrayList<Debt> getDebts() {
        return this.debts;
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }


}

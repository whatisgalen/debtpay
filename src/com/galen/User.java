package com.galen;

import java.util.ArrayList;

public class User {

    public String userName;
    public ArrayList<Debt> debts;
    public float money;

    //user object needs a numerical user_id and an array called "debts" of object class: Debt
    public User(String userName, float money) {
        this.userName = userName;
        this.debts = new ArrayList<Debt>();
        this.money = money;
    }

    public void addDebt(String debtName, float principal, double interestRate) {
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

    public float getMoney() {
        return this.money;
    }

    public void setMoney(float money) {
        this.money = money;
    }


}

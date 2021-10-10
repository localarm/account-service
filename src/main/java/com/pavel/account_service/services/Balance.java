package com.pavel.account_service.services;

import java.io.Serializable;

public class Balance implements Serializable {
    private Long balance;
    private boolean stored;

    public Balance(Long balance, boolean stored) {
        this.balance = balance;
        this.stored = stored;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public void addToBalance(Long amount) {
        balance+= amount;
    }
}

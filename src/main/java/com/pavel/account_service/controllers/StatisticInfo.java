package com.pavel.account_service.controllers;


public class StatisticInfo {
    private long total;
    private long current;

    public StatisticInfo(long total, long current) {
        this.total = total;
        this.current = current;
    }

    public long getTotal() {
        return total;
    }

    public long getCurrent() {
        return current;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setCurrent(long current) {
        this.current = current;
    }
}

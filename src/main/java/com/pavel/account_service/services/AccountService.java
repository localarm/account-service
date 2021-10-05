package com.pavel.account_service.services;

public interface AccountService {
    /**
     * Retrieves current balance or zero if addAmount() method was not called before for specified id
     *
     * @param id balance identifier
     * @throws AccountAccessException if failed to get value
     */
    Long getAmount(Integer id) throws AccountAccessException;

    /**
     * Increases balance or set if addAmount() method was called first time
     *
     * @param id balance identifier
     * @param value positive or negative value, which must be added to current balance
     * @throws AccountAccessException if failed to store value
     */
    void addAmount(Integer id, Long value) throws AccountAccessException;
}

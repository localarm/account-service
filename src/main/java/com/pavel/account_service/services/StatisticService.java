package com.pavel.account_service.services;

public interface StatisticService {

    /**Return number of times {@link AccountService#getAmount(Integer)} was processed at last 1 second period.
     * Value must be refreshed every second. Counting starts from service startup or reset @link #resetStatistic()}
     */
    long getCurrentCountOfGetAmount();

    /**Return number of times {@link AccountService#addAmount(Integer, Long)} was processed at last 1 second period.
     * Value must be refreshed every second. Counting starts from service startup or reset @link #resetStatistic()}
     */
    long getCurrentCountOfAddAmount();

    /**Return number of times {@link AccountService#getAmount(Integer)} was processed
     * from the service start or after statistic reset {@link #resetStatistic()}
     */
    long getTotalCountOfGetAmount();

    /**Return number of times {@link AccountService#addAmount(Integer, Long)} was processed
     * from the service start or after statistic reset {@link #resetStatistic()}
     */
    long getTotalCountOfAddAmount();

    /**Increment total invocation of {@link AccountService#getAmount(Integer)} by 1
     */
    void incrementTotalCountOfGetAmount();

    /**Increment total invocation of {@link AccountService#addAmount(Integer, Long)} by 1
     */
    void incrementTotalCountOfAddAmount();

    /**Reset current and total values to zero
     */
    void resetStatistic();
}

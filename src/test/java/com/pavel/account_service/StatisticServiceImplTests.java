package com.pavel.account_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.pavel.account_service.services.StatisticServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatisticServiceImplTests {

    private static StatisticServiceImpl statisticServiceImpl;

    @BeforeEach
    public void refreshStaticService() {
        statisticServiceImpl = new StatisticServiceImpl();
    }

    @Test
    public void incrementGetAmount() {
        assertEquals(0, statisticServiceImpl.getTotalCountOfGetAmount());
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        assertEquals(2, statisticServiceImpl.getTotalCountOfGetAmount());
    }

    @Test
    public void incrementAddAmount() {
        assertEquals(0, statisticServiceImpl.getTotalCountOfAddAmount());
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        assertEquals(3, statisticServiceImpl.getTotalCountOfAddAmount());
    }

    @Test
    public void updateStatistic() {
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.updateCurrentStatistic();
        assertEquals(2, statisticServiceImpl.getCurrentCountOfAddAmount());
        assertEquals(1, statisticServiceImpl.getCurrentCountOfGetAmount());
        assertEquals(1, statisticServiceImpl.getTotalCountOfGetAmount());
        assertEquals(2, statisticServiceImpl.getTotalCountOfAddAmount());
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.updateCurrentStatistic();
        assertEquals(1, statisticServiceImpl.getCurrentCountOfAddAmount());
        assertEquals(3, statisticServiceImpl.getCurrentCountOfGetAmount());
        assertEquals(4, statisticServiceImpl.getTotalCountOfGetAmount());
        assertEquals(3, statisticServiceImpl.getTotalCountOfAddAmount());
    }

    @Test
    public void resetStatistic() {
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfAddAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        statisticServiceImpl.incrementTotalCountOfGetAmount();
        assertEquals(3, statisticServiceImpl.getTotalCountOfGetAmount());
        assertEquals(4, statisticServiceImpl.getTotalCountOfAddAmount());
        statisticServiceImpl.resetStatistic();
        assertEquals(0, statisticServiceImpl.getTotalCountOfGetAmount());
        assertEquals(0, statisticServiceImpl.getTotalCountOfAddAmount());
    }
}

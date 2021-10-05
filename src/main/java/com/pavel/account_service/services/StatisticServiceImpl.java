package com.pavel.account_service.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticServiceImpl.class);
    private final AtomicLong totalGetCounter = new AtomicLong(0);
    private final AtomicLong totalAddCounter = new AtomicLong(0);
    private final Object lock = new Object();
    private volatile long currentGetCounter = 0;
    private volatile long prevTotalGetCounter = 0;
    private volatile long currentAddCounter = 0;
    private volatile long prevTotalAddCounter = 0;

    @Override
    public void incrementTotalCountOfGetAmount() {
        totalGetCounter.incrementAndGet();
    }

    @Override
    public void incrementTotalCountOfAddAmount() {
        totalAddCounter.incrementAndGet();
    }

    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void updateCurrentStatistic(){
        synchronized (lock) {
            long totalCountGetAmount = this.totalGetCounter.get();
            long totalCountAddAmount = this.totalAddCounter.get();
            currentAddCounter = totalCountAddAmount - prevTotalAddCounter;
            currentGetCounter = totalCountGetAmount - prevTotalGetCounter;
            prevTotalGetCounter = totalCountGetAmount;
            prevTotalAddCounter = totalCountAddAmount;
        }
        LOGGER.info("Statistic has been updated");
    }

    @Override
    public long getCurrentCountOfGetAmount() {
        return currentGetCounter;
    }

    @Override
    public long getCurrentCountOfAddAmount() {
        return currentAddCounter;
    }

    @Override
    public long getTotalCountOfGetAmount() {
        return totalGetCounter.get();
    }

    @Override
    public long getTotalCountOfAddAmount() {
        return totalAddCounter.get();
    }

    @Override
    public void resetStatistic () {
        synchronized (lock) {
            totalGetCounter.set(0);
            totalAddCounter.set(0);
            currentGetCounter = 0;
            prevTotalGetCounter = 0;
            currentAddCounter = 0;
            prevTotalAddCounter = 0;
        }
        LOGGER.info("Statistic has been reset");
    }
}

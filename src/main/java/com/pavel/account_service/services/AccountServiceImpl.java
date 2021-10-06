package com.pavel.account_service.services;

import com.hazelcast.map.IMap;
import com.pavel.account_service.dao.AccountDao;
import com.pavel.account_service.dao.WrappedDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl implements AccountService {

    private final IMap<Integer, Long> cache;
    private final AccountDao accountDAO;
    private final StatisticService statisticService;

    @Autowired
    public AccountServiceImpl(IMap<Integer, Long> cache, AccountDao accountDAO, StatisticService statisticService) {
        this.cache = cache;
        this.accountDAO = accountDAO;
        this.statisticService = statisticService;
    }

    @Override
    public Long getAmount(Integer id) throws AccountAccessException {
        try {
            return cache.get(id);
        } catch (WrappedDataAccessException ex) {
            throw new AccountAccessException("Failed to obtain amount by " + id + " id from database" , ex);
        } finally {
            statisticService.incrementTotalCountOfGetAmount();
        }
    }

    @Override
    public void addAmount(final Integer id,final Long value) throws AccountAccessException {
        try {
            cache.lock(id);
            Long cacheValue = cache.get(id);
            if (cacheValue == null) {
                accountDAO.insertBalance(id, value);
                //just "put" invoke unnecessary cache load from store
                cache.putTransient(id, value, 0, TimeUnit.MILLISECONDS);
            } else {
                accountDAO.updateBalance(id,cacheValue + value);
                //just "put" invoke unnecessary cache load from store
                cache.putTransient(id, cacheValue + value, 0, TimeUnit.MILLISECONDS);
            }
        } catch (WrappedDataAccessException ex)  {
            throw new AccountAccessException("Failed to set amount to id " + id + " in database", ex);
        }
        finally {
            cache.unlock(id);
            statisticService.incrementTotalCountOfAddAmount();
        }
    }
}

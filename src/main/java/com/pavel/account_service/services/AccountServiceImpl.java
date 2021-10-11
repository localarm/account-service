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

    private final IMap<Integer, Balance> cache;
    private final AccountDao accountDAO;
    private final StatisticService statisticService;

    @Autowired
    public AccountServiceImpl(IMap<Integer, Balance> cache, AccountDao accountDAO, StatisticService statisticService) {
        this.cache = cache;
        this.accountDAO = accountDAO;
        this.statisticService = statisticService;
    }

    @Override
    public Long getAmount(Integer id) throws AccountAccessException {
        try {
            return cache.get(id).getBalance();
        } catch (WrappedDataAccessException ex) {
            throw new AccountAccessException("Failed to obtain amount by " + id + " id from database" , ex.getCause());
        } finally {
            statisticService.incrementTotalCountOfGetAmount();
        }
    }

    @Override
    public void addAmount(Integer id, Long value) throws AccountAccessException {
        try {
            cache.lock(id);
            Balance cacheBalance = cache.get(id);
            if (cacheBalance.isStored()) {
                cacheBalance.addToBalance(value);
                accountDAO.updateBalance(id,cacheBalance.getBalance());
            } else {
                accountDAO.insertBalance(id, value);
                cacheBalance.setBalance(value);
            }
            cache.putTransient(id, cacheBalance, 0, TimeUnit.MILLISECONDS);
        } catch (WrappedDataAccessException ex)  {
            throw new AccountAccessException("Failed to set amount to id " + id + " in database", ex.getCause());
        } catch (DataAccessException ex) {
            throw new AccountAccessException("Failed to set amount to id " + id + " in database", ex);
        }
        finally {
            cache.unlock(id);
            statisticService.incrementTotalCountOfAddAmount();
        }
    }
}

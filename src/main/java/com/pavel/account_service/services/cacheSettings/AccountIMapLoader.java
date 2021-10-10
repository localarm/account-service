package com.pavel.account_service.services.cacheSettings;

import com.hazelcast.map.MapLoader;
import com.pavel.account_service.dao.AccountDao;
import com.pavel.account_service.dao.WrappedDataAccessException;
import com.pavel.account_service.services.Balance;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.Collection;
import java.util.Map;

public class AccountIMapLoader implements MapLoader<Integer, Balance> {

    private final AccountDao accountDAO;

    public AccountIMapLoader(AccountDao accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Balance load(Integer key) {
        try {
            return new Balance(accountDAO.findBalance(key), true);
        } catch (EmptyResultDataAccessException ex) {
            return new Balance (0L, false);
        } catch (DataAccessException ex) {
            throw new WrappedDataAccessException("Wrap DataAccessException", ex);
        }
    }

    @Override
    public Map<Integer, Balance> loadAll(Collection<Integer> keys) {
        return null;
    }

    @Override
    public Iterable<Integer> loadAllKeys() {
        return null;
    }
}

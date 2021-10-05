package com.pavel.account_service.services.cacheSettings;

import com.hazelcast.map.MapLoader;
import com.pavel.account_service.dao.AccountDao;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collection;
import java.util.Map;

public class AccountIMapLoader implements MapLoader<Integer, Long> {

    private final AccountDao accountDAO;

    public AccountIMapLoader(AccountDao accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public Long load(Integer key) {
        try {
            return accountDAO.findBalance(key);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Map<Integer, Long> loadAll(Collection<Integer> keys) {
        return null;
    }

    @Override
    public Iterable<Integer> loadAllKeys() {
        return null;
    }
}

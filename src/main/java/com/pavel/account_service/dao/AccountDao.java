package com.pavel.account_service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class AccountDao {

    private final JdbcTemplate template;

    @Autowired
    public AccountDao(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    public Long findBalance(Integer id) {
        return template.queryForObject("SELECT balance FROM accounts Where id = ?", Long.class, id);
    }

    public void insertBalance(Integer id, Long balance) {
        template.update("INSERT INTO accounts(id, balance) VALUES(?, ?)", id, balance);
    }

    public void updateBalance(Integer id, Long balance) {
        template.update("UPDATE accounts SET balance = ? WHERE id = ?", balance, id);
    }

}

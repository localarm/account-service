package com.pavel.account_service.dao;


/**Wrapper over {@link org.springframework.dao.DataAccessException}. Need to fix Hazelcast's wrong exception rethrowing.
 * For example, {@link org.springframework.jdbc.BadSqlGrammarException} swallowed
 */
public class WrappedDataAccessException extends RuntimeException {

    public WrappedDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.pavel.account_service.dao;


/**Wrapper over {@link org.springframework.dao.DataAccessException}. Need to fix Hazelcast's wrong exception rethrowing.
 * {@link com.hazelcast.internal.util.ExceptionUtil#tryCreateExceptionWithMessageAndCause(Class, String, Throwable)}
 * can't recreate {@link org.springframework.jdbc.BadSqlGrammarException} and return null.
 */
public class WrappedDataAccessException extends RuntimeException {

    public WrappedDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

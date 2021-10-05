package com.pavel.account_service.services;

public class AccountAccessException extends Exception {
    public AccountAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.ridelink.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String field, String value) {
        super("Account not found with " + field + ": " + value);
    }
}

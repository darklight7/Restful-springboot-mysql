package com.example.Demo.exception;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = 2198302880102958845L;

    public UserServiceException(String message){

        super(message);
    }
}

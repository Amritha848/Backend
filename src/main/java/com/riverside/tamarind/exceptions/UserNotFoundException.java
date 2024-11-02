package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message){
        super(message);
    }
}

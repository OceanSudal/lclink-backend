package com.sudal.lclink.exception;

public class AlreadyExistElementException extends RuntimeException{
    public AlreadyExistElementException(String message) {
        super(message);
    }
    public AlreadyExistElementException(String message, Throwable cause) { super(message, cause); }

}

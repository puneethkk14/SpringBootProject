package com.library.exception;

public class MemberNotActiveException extends RuntimeException {
    public MemberNotActiveException(String message) {
        super(message);
    }
}

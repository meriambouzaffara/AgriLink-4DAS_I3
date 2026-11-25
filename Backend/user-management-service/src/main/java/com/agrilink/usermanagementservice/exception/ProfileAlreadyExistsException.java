// ProfileAlreadyExistsException.java
package com.agrilink.usermanagementservice.exception;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException(String message) { super(message); }
}
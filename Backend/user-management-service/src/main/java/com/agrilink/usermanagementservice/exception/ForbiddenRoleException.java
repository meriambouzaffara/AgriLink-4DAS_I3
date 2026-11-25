// ForbiddenRoleException.java
package com.agrilink.usermanagementservice.exception;

public class ForbiddenRoleException extends RuntimeException {
    public ForbiddenRoleException(String message) { super(message); }
}
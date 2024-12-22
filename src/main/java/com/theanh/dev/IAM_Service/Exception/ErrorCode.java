package com.theanh.dev.IAM_Service.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION( "An unexpected error occurred. Please try again later!", HttpStatus.INTERNAL_SERVER_ERROR),
    SESSION_EXPIRED("Session has expired. Please log in again!", HttpStatus.BAD_REQUEST),

    FIELD_REQUIRED( "Some fields are missing or incomplete. " +
            "Ensure all required fields are filled!", HttpStatus.UNPROCESSABLE_ENTITY),
    INSECURE_PASSWORD("Password must be at least 8 characters long, " +
            "contain at least one number, " +
            "one uppercase letter, " +
            "and one special character!", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_CREDENTIALS("Oops! That didn’t work. Please check your email and password and try again!", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_OTP("Invalid OTP, please check and try again!", HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_NOT_MATCH("New password and confirmation password do not match!", HttpStatus.UNPROCESSABLE_ENTITY),
    EXISTED_USER("The email address provided is already in use!" +
            "If you forgot your password, try recovering your account!", HttpStatus.BAD_REQUEST),
    NOT_EXISTED_USER( "Oops! We couldn't find your account with that email address. " +
            "Make sure it's spelled correctly or create a new one!", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED("Login failed. Please check your email and password!", HttpStatus.UNAUTHORIZED)
    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

package com.theanh.dev.IAM_Service.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION( "Uncategorized error!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY("Invalid message key!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("Invalid or expired token!", HttpStatus.UNAUTHORIZED),

    TAKEN_USERNAME("This username is already taken", HttpStatus.BAD_REQUEST),
    USER_EXISTED("User existed!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED( "User not existed!", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User not found!", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED("Unauthenticated!", HttpStatus.UNAUTHORIZED),

    INCOMPLETE_DETAIL( "User details are incomplete!", HttpStatus.BAD_REQUEST),
    INVALID_USERNAME("Username must be between 3 and 20 characters!", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("Password must be at least 8 characters long, contain at least one number, one uppercase letter, and one special character.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("Invalid email format", HttpStatus.BAD_REQUEST),
    INVALID_DOB("Date of birth must be a past date", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("Phone number must be 10 digits", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

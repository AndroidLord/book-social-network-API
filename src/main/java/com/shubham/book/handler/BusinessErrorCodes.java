package com.shubham.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    No_Code (0, HttpStatus.NOT_IMPLEMENTED, "No code"),


    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "new password is incorrect"),
    INCORRECT_CURRENT_PASSOWRD(303, HttpStatus.BAD_REQUEST, "current password is incorrect"),


    ACCOUNT_LOCKED (302, HttpStatus.FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED (303, HttpStatus.FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS (304, HttpStatus.UNAUTHORIZED, "Login and / or password is inc"),
    ;

    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code,HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}

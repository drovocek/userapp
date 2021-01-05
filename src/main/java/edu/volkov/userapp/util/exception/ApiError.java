package edu.volkov.userapp.util.exception;

import lombok.Getter;

@Getter
public class ApiError {

    private final String url;
    private final ErrorType type;
    private final String typeMessage;
    private final String[] details;

    public ApiError(CharSequence url, ErrorType type, String typeMessage, String... details) {
        this.url = url.toString();
        this.type = type;
        this.typeMessage = typeMessage;
        this.details = details;
    }
}
package edu.volkov.userapp.util.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

//    private HttpStatus status;
//    private String message;
//    private List<String> errors;
//
//    public ApiError(HttpStatus status, String message, List<String> errors) {
//        super();
//        this.status = status;
//        this.message = message;
//        this.errors = errors;
//    }
//
//    public ApiError(HttpStatus status, String message, String error) {
//        super();
//        this.status = status;
//        this.message = message;
//        errors = Collections.singletonList(error);
//    }
}
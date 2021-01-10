package edu.volkov.userapp.web;

import edu.volkov.userapp.to.UserPackage;
import edu.volkov.userapp.util.exception.ApiError;
import edu.volkov.userapp.util.exception.ErrorType;
import edu.volkov.userapp.util.exception.IllegalRequestDataException;
import edu.volkov.userapp.util.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;

import static edu.volkov.userapp.to.PackageType.ERROR;
import static edu.volkov.userapp.util.exception.ErrorType.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final HashMap<String, String> messCodes = new HashMap<>();

    static {
        messCodes.put("exception.duplicateEmail", "User with this email already exists");
        messCodes.put("exception.idDosNotExist", "User with this id is not exist");
    }

    @MessageExceptionHandler(NotFoundException.class)
    @SendToUser("/queue/errors")
    public UserPackage handleNotFound() {
        log.info("\n<< handleNotFound >>");
        return getApiError(DATA_NOT_FOUND, "Entity not found");
    }

    @MessageExceptionHandler(IllegalRequestDataException.class)
    @SendToUser("/queue/errors")
    public UserPackage handleIllegalRequestData() {
        log.info("\n<< handleIllegalRequestData >>");
        return getApiError(VALIDATION_ERROR, "Bad request data");
    }

    @MessageExceptionHandler(BindException.class)
    @SendToUser("/queue/errors")
    public UserPackage bindValidationError(BindException e) {
        log.info("\n<< bindValidationError >>");

        String[] details = e.getBindingResult().getFieldErrors().stream()
                .map(err -> {
                    if (messCodes.containsKey(err.getCode())) {
                        return messCodes.get(err.getCode());
                    } else {
                        return err.getDefaultMessage();
                    }
                })
                .toArray(String[]::new);

        return getApiError(VALIDATION_ERROR, details);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public UserPackage handleAll(Exception ex) {
        log.info("\n<<handleAll>>");
        return getApiError(APP_ERROR, "Some unknown shit");
    }

    private UserPackage getApiError(ErrorType errorType, String... details) {
        log.info("\n<<getApiError>>");

        ApiError apiError = new ApiError(
                errorType,
                errorType.getErrorCode(),
                details != null ? details : new String[0]
        );

        return UserPackage.builder()
                .packageType(ERROR)
                .apiError(apiError).build();
    }
}
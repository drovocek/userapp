package edu.volkov.userapp.web;

import edu.volkov.userapp.util.exception.ApiError;
import edu.volkov.userapp.util.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static edu.volkov.userapp.util.exception.ErrorType.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
//@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class CustomRestExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(HttpServletRequest req) {
        log.info("\n<<handleResourceNotFound>>");
        return getApiError(req, DATA_NOT_FOUND, "Unknown entity");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.info("\n<<handleConstraintViolation>>");

        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }

        return getApiError(req, VALIDATION_ERROR, errors.toArray(new String[0]));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.info("\n<<handleDataIntegrityViolation>>");

        String data = Objects.requireNonNull(ex.getMessage()).contains("email") ? "Duplicate email" : "";
        return getApiError(req, DATA_ERROR, data);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest req) {
        log.info("\n<<handleAll>>");

        try {
            ConstraintViolationException exx = (ConstraintViolationException) ex.getCause().getCause();
            return handleConstraintViolation(exx, req);
        } catch (Exception e) {
            return getApiError(req, APP_ERROR, "Some unknown shit");
        }
    }

    private ResponseEntity<ApiError> getApiError(HttpServletRequest req, ErrorType errorType, String... details) {
        log.info("\n<<getApiError>>");
        return ResponseEntity.status(errorType.getStatus())
                .body(new ApiError(
                        req.getRequestURL(),
                        errorType,
                        errorType.getErrorCode(),
                        details != null ? details : new String[0])
                );
    }
}
package edu.volkov.userapp.web;

import edu.volkov.userapp.to.UserPackage;
import edu.volkov.userapp.to.PackageType;
import edu.volkov.userapp.util.exception.ApiError;
import edu.volkov.userapp.util.exception.ErrorType;
import edu.volkov.userapp.util.exception.IllegalRequestDataException;
import edu.volkov.userapp.util.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static edu.volkov.userapp.util.exception.ErrorType.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
//@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class GlobalExceptionHandler {

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
                .map(DefaultMessageSourceResolvable::getCode)
                .toArray(String[]::new);

        return getApiError(VALIDATION_ERROR, details);
    }
//
//    @MessageExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
//        log.info("\n<<handleConstraintViolation>>");
//
//        List<String> errors = new ArrayList<String>();
//        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
//            errors.add(violation.getMessage());
//        }
//
//        return getApiError(req, VALIDATION_ERROR, errors.toArray(new String[0]));
//    }
//
//    @MessageExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
//        log.info("\n<<handleDataIntegrityViolation>>");
//
//        String data = Objects.requireNonNull(ex.getMessage()).contains("email") ? "Duplicate email" : "";
//        return getApiError(req, DATA_ERROR, data);
//    }
//
//    @MessageExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest req) {
//        log.info("\n<<handleAll>>");
//
//        try {
//            ConstraintViolationException exx = (ConstraintViolationException) ex.getCause().getCause();
//            return handleConstraintViolation(exx, req);
//        } catch (Exception e) {
//            return getApiError(req, APP_ERROR, "Some unknown shit");
//        }
//    }

    private UserPackage getApiError(ErrorType errorType, String... details) {
        log.info("\n<<getApiError>>");
        UserPackage packageWithErr = new UserPackage();

        ApiError apiError = new ApiError(
                errorType,
                errorType.getErrorCode(),
                details != null ? details : new String[0]
                );

        packageWithErr.setApiError(apiError);
        packageWithErr.setPackageType(PackageType.ERROR.name());

        return packageWithErr;
    }
}
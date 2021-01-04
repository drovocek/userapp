package edu.volkov.userapp.web;

import edu.volkov.userapp.util.ValidationUtil;
import edu.volkov.userapp.util.exception.ApplicationException;
import edu.volkov.userapp.util.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView wrongRequest(HttpServletRequest req, NoHandlerFoundException e) {
        return logAndGetExceptionView(req, e, false, ErrorType.WRONG_REQUEST, null);
    }

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView updateRestrictionException(HttpServletRequest req, ApplicationException appEx) {
        return logAndGetExceptionView(req, appEx, false, appEx.getType(), appEx.getMsgCode());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!! req " + req);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!! e " + e);
        log.error("Exception at request " + req.getRequestURL(), e);
        return logAndGetExceptionView(req, e, true, ErrorType.APP_ERROR, null);
    }

    private ModelAndView logAndGetExceptionView(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType, String code) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logException, errorType);

        Map<String, Object> errResp = new HashMap();
        errResp.put("exception", rootCause);
        errResp.put("message", code != null ? code : ValidationUtil.getMessage(rootCause));
        errResp.put("typeMessage", errorType.getErrorCode());
        errResp.put("status", errorType.getStatus());

        ModelAndView mav = new ModelAndView("exception", errResp);

        mav.setStatus(errorType.getStatus());
        return mav;
    }
}

package com.ivanrl.yaet;

import com.ivanrl.yaet.domain.ValidationError;
import com.ivanrl.yaet.web.notifications.NotificationGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.ivanrl.yaet.web.HtmxConstants.RESPONSE_HEADER_RESWAP;
import static com.ivanrl.yaet.web.HtmxConstants.RESPONSE_HEADER_RETARGET;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    /** Selector with the id of the HTML element that holds all the notifications **/
    private static final String GROWL_SELECTOR = "#growl";


    /**
     * Logs the exception details with level ERROR
     * @param e the exception being handled
     * @param problem the {@link ProblemDetail} object sent back as the response
     */
    private static void logHandledException(Exception e,
                                            ProblemDetail problem) {
        log.error("{} handled\n\tResponse: {}\n\n{}",
                  e.getClass().getSimpleName(),
                  problem,
                  Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n\t")));
    }

    @ExceptionHandler
    public ProblemDetail handleBadRequest(BadRequestException bre) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, bre.getMessage());
        problem.setTitle(bre.getTitle());
        logHandledException(bre, problem);
        return problem;
    }

    @ExceptionHandler
    public ProblemDetail handleValidationError(ValidationError ve) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ve.getMessage());
        problem.setTitle("Validation error");
        logHandledException(ve, problem);
        return problem;
    }


    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e,
                                        HttpServletResponse response) {
        response.setHeader(RESPONSE_HEADER_RETARGET, GROWL_SELECTOR);
        response.setHeader(RESPONSE_HEADER_RESWAP, "beforeend transition:true");
        var map = new HashMap<String, Object>();
        map.put("notification", NotificationGenerator.error(e.getMessage()));
        return new ModelAndView("notification", map);
    }
}

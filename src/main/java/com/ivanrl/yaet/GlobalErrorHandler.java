package com.ivanrl.yaet;

import com.ivanrl.yaet.domain.ValidationError;
import com.ivanrl.yaet.web.notifications.NotificationGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {


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

    /**
     * Logs the exception details with level ERROR
     * @param e the exception being handled
     */
    private static void logHandledException(Exception e) {
        log.error("{} handled\n\tResponse: {}",
                  e.getClass().getSimpleName(),
                  Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n\t")));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleBadRequest(BadRequestException bre) {
        logHandledException(bre);
        return NotificationGenerator.warning(bre.getMessage());
    }

    // TODO This issue is currently only used for a modal dialog, need to find how to deal with those
    @ExceptionHandler
    public ProblemDetail handleValidationError(ValidationError ve) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ve.getMessage());
        problem.setTitle("Validation error");
        logHandledException(ve, problem);
        return problem;
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleException(Exception e) {
        log.error("An unexpected error occurred: {}\n{}",
                  e.getMessage(),
                  Arrays.stream(e.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.joining("\n")));
        return NotificationGenerator.error("An unexpected error occurred");
    }
}

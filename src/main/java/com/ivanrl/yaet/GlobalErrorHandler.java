package com.ivanrl.yaet;

import com.ivanrl.yaet.domain.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public ProblemDetail handleException(Exception e) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                                                       "Something went wrong. Please, try again.");
        problem.setTitle("Oops!");
        logHandledException(e, problem);
        return problem;
    }
}

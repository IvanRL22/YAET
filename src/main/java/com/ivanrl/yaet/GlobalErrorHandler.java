package com.ivanrl.yaet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException bre) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, bre.getMessage());
        problem.setTitle(bre.getTitle());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception e) {
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please, try again.");
        problem.setTitle("Oops!");
        return problem;
    }
}

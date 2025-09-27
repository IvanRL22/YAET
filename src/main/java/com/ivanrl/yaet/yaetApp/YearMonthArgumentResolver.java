package com.ivanrl.yaet.yaetApp;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.YearMonth;

/**
 * Enables the use of {@link YearMonth} as a path variable or request parameter
 * @see PathVariable
 * @see RequestParam
 */
public class YearMonthArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(PathVariable.class)
                || parameter.hasParameterAnnotation(RequestParam.class)) {
            return false;
        }

        return YearMonth.class.isAssignableFrom(parameter.getContainingClass());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        var parameterList = webRequest.getParameterMap().get(parameter.getParameterName());

        return switch (parameterList.length) {
            case 0 -> throw new RuntimeException("Parameter was not found"); // TODO Find proper exception
            case 1 -> YearMonth.parse(parameterList[0]);
            default -> throw new RuntimeException("More than one parameter was found"); // TODO Find proper exception
        };
    }
}

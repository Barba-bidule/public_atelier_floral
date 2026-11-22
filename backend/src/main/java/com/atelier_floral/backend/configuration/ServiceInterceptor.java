package com.atelier_floral.backend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
public class ServiceInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Map<String, String[]> parameters = request.getParameterMap();
        StringBuilder builder = new StringBuilder();
        parameters.forEach((parameterName, values) -> {
            builder.append(parameterName)
                    .append(" : ")
                    .append(
                            values.length == 1 ?
                                    (values[0] == null || values[0].trim().isEmpty() ? "<empty>" : values[0])
                                    : Arrays.toString(Arrays.stream(values).map(value -> value == null || value.trim().isEmpty() ? "<empty>" : value).toArray())
                    )
                    .append(", ");
        });
        LOGGER.info(parameters.isEmpty() ? String.format("[%s %s]", request.getMethod(), request.getRequestURI()) : String.format("[%s %s] -> %s",request.getMethod(), request.getRequestURI(), formatLoggerContent(builder)));
        return true;
    }

    private StringBuilder formatLoggerContent(StringBuilder builder) {
        if(!builder.isEmpty()) builder.replace(builder.length() - 2, builder.length() - 1, "");
        return builder;
    }

}
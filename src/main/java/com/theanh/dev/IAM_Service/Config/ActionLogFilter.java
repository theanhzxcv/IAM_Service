package com.theanh.dev.IAM_Service.Config;

import com.theanh.dev.IAM_Service.Exception.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@WebFilter("/api/**")
@RequiredArgsConstructor
@Component
public class ActionLogFilter extends OncePerRequestFilter {

    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
//        logRequest(request);
//
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception ex) {
//            logException(ex);
//            throw ex;
//        }
//
//        logResponse(response);
    }

    private void logRequest(HttpServletRequest request) throws IOException {
//        StringBuilder logMessage = new StringBuilder();
//        logMessage.append("Request: ").append(request.getMethod())
//                .append(" ").append(request.getRequestURI())
//                .append(" Params: ").append(request.getQueryString());
//
//        String body = request.getReader().lines()
//                .reduce("", (accumulator, actual) -> accumulator + actual);
//        if (body.contains("password")) {
//            body = body.replaceAll("password=[^&]*", "password=****");
//        }
//        logMessage.append(" Body: ").append(body);
//
//        logger.info(logMessage.toString());
    }

    private void logResponse(HttpServletResponse response) {
        String logMessage = "Response Status: " + response.getStatus();
        logger.info(logMessage);
    }

    public void logException(Exception ex) {
        logger.error("Exception occurred: ", ex);
    }
}

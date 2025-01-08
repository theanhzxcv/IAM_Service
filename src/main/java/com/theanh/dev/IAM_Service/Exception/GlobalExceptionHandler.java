package com.theanh.dev.IAM_Service.Exception;

import com.theanh.dev.IAM_Service.Config.ActionLogFilter;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponse;
import com.theanh.dev.IAM_Service.Dtos.Response.ApiResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        catchException(exception);

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus())
                .body(ApiResponseBuilder
                .buildErrorResponse(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus(),
                ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()));
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        catchException(exception);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
                .buildErrorResponse(errorCode.getHttpStatus(),
                        errorCode.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String key = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(key);
        catchException(exception);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
                .buildErrorResponse(errorCode.getHttpStatus(),
                        errorCode.getMessage()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.INSUFFICIENT_PERMISSIONS;
        catchException(exception);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(ApiResponseBuilder
                .buildErrorResponse(errorCode.getHttpStatus(),
                        errorCode.getMessage()));
    }

    private void catchException(Exception exception) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            requestAttributes.setAttribute(
                    "custom_exception_atr", exception, RequestAttributes.SCOPE_REQUEST);
        }
    }

}

package org.homedecoration.handler;

import org.homedecoration.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1️⃣ 参数校验错误（@Valid）
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidException(
            org.springframework.web.bind.MethodArgumentNotValidException e) {

        String msg = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ApiResponse.fail(400, msg);
    }

    /**
     * 2️⃣ 登录 / 鉴权相关错误
     * 约定：Service 中抛 IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthException(IllegalArgumentException e) {
        return ApiResponse.fail(401, e.getMessage());
    }

    /**
     * 3️⃣ 其它运行时异常（兜底）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        return ApiResponse.fail(500, e.getMessage());
    }


}


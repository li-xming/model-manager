package com.example.datamodel.exception;

import com.example.datamodel.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 *
 * @author DataModel Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return ResponseVO.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return ResponseVO.error(400, message);
    }

    /**
     * 处理参数校验异常（@ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    public ResponseVO<?> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        return ResponseVO.error(400, message);
    }

    /**
     * 处理参数校验异常（@RequestParam）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseVO<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("参数校验异常：{}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.iterator().next().getMessage();
        return ResponseVO.error(400, message);
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return ResponseVO.error("系统异常，请联系管理员");
    }
}


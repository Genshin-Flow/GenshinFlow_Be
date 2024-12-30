package com.next.genshinflow.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 사용자 정의 예외
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
        log.error("BusinessLogicException: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getCode(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 유효성 검사 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(FieldError::getDefaultMessage)
            .orElse("잘못된 요청입니다.");

        ErrorResponse errorResponse = new ErrorResponse(400, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 권한 부족 예외
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access Denied: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(403, "접근 권한이 없습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // 잘못된 JSON 요청
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Invalid JSON format: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, "잘못된 JSON 형식입니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 검증 실패 예외
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Constraint violation: {}", e.getMessage());
        String errorMessage = e.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = new ErrorResponse(400, errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 잘못된 인자 예외
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 데이터 무결성 예외
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(409, "데이터 무결성 위반 오류");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 타임아웃 예외
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException e) {
        log.error("Request Timeout: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(408, "요청 시간이 초과되었습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    // null pointer 예외
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("Null pointer exception: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(500, "서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // sql 예외
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        log.error("SQL exception: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(500, "데이터베이스 오류가 발생했습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 요청 경로 잘못된 경우 예외 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("Invalid request path: {}", e.getRequestURL());
        ErrorResponse errorResponse = new ErrorResponse(404, "요청하신 경로를 찾을 수 없습니다: " + e.getRequestURL());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}

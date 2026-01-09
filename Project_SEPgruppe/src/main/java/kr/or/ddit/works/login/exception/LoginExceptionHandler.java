package kr.or.ddit.works.login.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LoginExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, Object>> handle(LoginException e) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", e.getMessage()));
    }
}
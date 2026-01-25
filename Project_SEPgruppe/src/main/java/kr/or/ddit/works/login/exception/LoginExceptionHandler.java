package kr.or.ddit.works.login.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// LoginException 전용 예외 처리기
// Controller에서 try~catch 사용하지 않고 JSON 응답을 일관되게 유지하기 위한 Handler
@RestControllerAdvice
public class LoginExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, Object>> handle(LoginException e) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("message", e.getMessage()));
    }
}
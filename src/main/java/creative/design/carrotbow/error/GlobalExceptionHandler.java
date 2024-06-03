package creative.design.carrotbow.error;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "ACCESS_LOG")
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<?> handleCustomException(JWTVerificationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());

        log.info("Unauthorized");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<?> handleCustomException(InvalidAccessException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid Access");
        body.put("message", ex.getMessage());

        log.info("Invalid Access: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleCustomException(NotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found Object");
        body.put("message", ex.getMessage());

        log.info("Not Found Object: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PayProcessException.class)
    public ResponseEntity<?> handleCustomException(PayProcessException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Error While Payment");
        body.put("message", ex.getMessage());

        log.info("Error While Payment: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Exist Type");
        body.put("message", ex.getMessage());

        log.info("Not Exist Type: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(WrongApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(WrongApplicationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Wrong Application");
        body.put("message", ex.getMessage());

        log.info("Wrong Application: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

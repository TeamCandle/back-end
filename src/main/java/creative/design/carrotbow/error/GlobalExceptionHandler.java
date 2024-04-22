package creative.design.carrotbow.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<?> handleCustomException(InvalidAccessException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Invalid Access");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleCustomException(NotFoundException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found Object");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(PayProcessException.class)
    public ResponseEntity<?> handleCustomException(PayProcessException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Error While Payment");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Exist Type");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(WrongApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(WrongApplicationException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Wrong Application");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

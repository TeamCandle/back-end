package creative.design.carrotbow.error;

public class PayProcessException extends RuntimeException{

    public PayProcessException(String message) {
        super(message);
    }

    public PayProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayProcessException(Throwable cause) {
        super(cause);
    }
}

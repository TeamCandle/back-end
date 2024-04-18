package creative.design.carrotbow.error;

public class InvalidAccessException extends RuntimeException{
    public InvalidAccessException() {
        super();
    }

    public InvalidAccessException(String s) {
        super(s);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

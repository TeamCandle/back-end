package creative.design.carrotbow.error;

public class WrongApplicationException extends NullPointerException{

    public WrongApplicationException() {
        super();
    }

    public WrongApplicationException(String s) {
        super(s);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

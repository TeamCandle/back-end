package creative.design.carrotbow.error;

public class NotFoundException extends NullPointerException{

    public NotFoundException() {
        super();
    }

    public NotFoundException(String s) {
        super(s);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

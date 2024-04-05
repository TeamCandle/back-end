package creative.design.carrotbow.exception;

public class DogNotFoundException extends NullPointerException{

    public DogNotFoundException() {
        super();
    }

    public DogNotFoundException(String s) {
        super(s);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

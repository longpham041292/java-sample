package asia.cmg.f8.common.exception;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
public class InvalidTimeRangeException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    
    private final String message;

    public InvalidTimeRangeException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

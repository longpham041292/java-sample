package asia.cmg.f8.commerce.exception;

public class DuplicateProductException extends CommerceException {

    private static final long serialVersionUID = 1L;
    
    public DuplicateProductException(final String message) {
        super(message);
    }

    
}

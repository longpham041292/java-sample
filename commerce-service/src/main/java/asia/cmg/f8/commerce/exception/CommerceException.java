package asia.cmg.f8.commerce.exception;

public class CommerceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;
    
    
    public CommerceException(final String message) {
        super();
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
}

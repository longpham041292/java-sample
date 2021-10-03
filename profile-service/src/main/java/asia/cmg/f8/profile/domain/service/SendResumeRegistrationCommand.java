package asia.cmg.f8.profile.domain.service;

/**
 * @author tung.nguyenthanh
 */
public class SendResumeRegistrationCommand {
    private String userId;

    public SendResumeRegistrationCommand(final String userId) {
        super();
        this.userId = userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}

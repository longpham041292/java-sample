package asia.cmg.f8.user.service;


/**
 * Created by tuong.le on 11/3/16.
 */
public class SubmitDocumentEmailCommand {

    private String userId;

    public SubmitDocumentEmailCommand(final String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}

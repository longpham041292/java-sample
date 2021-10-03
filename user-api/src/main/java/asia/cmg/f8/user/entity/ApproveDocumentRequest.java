package asia.cmg.f8.user.entity;

/**
 * Created on 11/10/16.
 */
public class ApproveDocumentRequest {
    private String userId;
    private boolean approved;
    private String levelId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(final String levelId) {
        this.levelId = levelId;
    }
}

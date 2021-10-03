package asia.cmg.f8.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApproveDocumentResponse {
    private Boolean success;
    private Long approvedDate;
    private String displayApprovedDate;

    public ApproveDocumentResponse(final Boolean success, final Long approvedDate, final String displayApprovedDate) {
        this.success = success;
        this.approvedDate = approvedDate;
        this.displayApprovedDate = displayApprovedDate;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(final Boolean success) {
        this.success = success;
    }

    public Long getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(final Long approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getDisplayApprovedDate() { return displayApprovedDate; }

    public void setDisplayApprovedDate(final String displayApprovedDate) { this.displayApprovedDate = displayApprovedDate; }
}

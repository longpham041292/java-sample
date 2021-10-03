package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 12/26/16.
 */
public class TransferRequest {

    @JsonProperty("session_package_uuid")
    private String sessionPackageUuid;

    @JsonProperty("new_pt_uuid")
    private String newTrainerUuid;

    public String getSessionPackageUuid() {
        return sessionPackageUuid;
    }

    public void setSessionPackageUuid(final String sessionPackageUuid) {
        this.sessionPackageUuid = sessionPackageUuid;
    }

    public String getNewTrainerUuid() {
        return newTrainerUuid;
    }

    public void setNewTrainerUuid(final String newTrainerUuid) {
        this.newTrainerUuid = newTrainerUuid;
    }
}

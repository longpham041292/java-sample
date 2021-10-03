package asia.cmg.f8.common.spec.session;

import asia.cmg.f8.common.spec.Identifiable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Represent a training session.
 * <p>
 * Created on 11/4/16.
 */
public interface Session extends Identifiable {

    @JsonProperty("owner_uuid")
    String getOwnerUuid();

    @JsonProperty("trainer_uuid")
    String getTrainerUuid();

    @JsonProperty("start_time")
    Date getStartTime();

    @JsonProperty("end_time")
    Date getEndTime();

    @JsonProperty("status")
    Status getStatus();

    enum Status {
        USER_CANCELLED, TRAINER_CANCELLED, BURNED, PENDING, CONFIRM
    }
}

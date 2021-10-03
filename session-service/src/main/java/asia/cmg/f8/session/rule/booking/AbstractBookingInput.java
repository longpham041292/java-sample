package asia.cmg.f8.session.rule.booking;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.service.ValidationService;

/**
 * Created on 12/10/16.
 */
@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC
)
@JsonSerialize(as = BookingInput.class)
@JsonDeserialize(as = BookingInput.class)
public abstract class AbstractBookingInput {

    abstract Set<ReservationSlot> getReservationSlotList();

    abstract String getUserId();

    abstract String getTrainerId();
    
    @Nullable
    abstract String getPackageUuid();

    @Nullable
    abstract Account getAccount();

    @Nullable
    abstract ValidationService getValidationService();
}

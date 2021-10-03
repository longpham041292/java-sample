package asia.cmg.f8.session.dto;

import java.util.Date;
import javax.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC)
public interface AbstractSessionExport {

	@Nullable
	String getSessionId();
	
	@Nullable
	String getSessionUuid();
	
	@Nullable
	Date getSessionStartTime();
	
	@Nullable
	Date getSessionEndTime();
	
	@Nullable
	String getSessionStatus();
	
	@Nullable
	String getUserUuid();
	
	@Nullable
	String getUserFullname();
	
	@Nullable
	String getTrainerUuid();
	
	@Nullable
	String getTrainerFullname();
	
	@Nullable
	String getTrainerLevel();
	
	@Nullable
	String getBookingClubName();
	
	@Nullable
	String getBookingClubAddress();
	
	@Nullable
	String getChekingClubName();
	
	@Nullable
	String getCheckingClubAddress();
}

package asia.cmg.f8.report.dto;

/**
 * Created on 12/21/16.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public enum SessionAction {
	NO_SHOW, APPROVED, CHECK_IN, CANCEL, USER_CANCEL_WITHIN_24H, PT_CANCEL, CREATED, BOOKED, TRANSFER, AUTO_EXPIRED,
	DEACTIVATED, AUTO_DEDUCTED, ETICKET_UPGRADED, AUTO_CANCELED, ADMIN_CANCELLED, ADMIN_COMPLETED, AUTO_BURNED;
}
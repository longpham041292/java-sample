package asia.cmg.f8.session.entity;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 12/21/16.
 */
public enum SessionPackageStatus {
	VALID, INVALID, TRANSFERRED, INACTIVE;

    public static List<SessionPackageStatus> getValidSessionPackageStatus() {
        return Arrays.asList(
                SessionPackageStatus.VALID,
                SessionPackageStatus.TRANSFERRED);

    }

	public static List<SessionPackageStatus> getAdminValidSessionPackageStatus() {
		return Arrays.asList(SessionPackageStatus.VALID, SessionPackageStatus.TRANSFERRED,
				SessionPackageStatus.INACTIVE);

	}
}

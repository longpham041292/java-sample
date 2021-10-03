package asia.cmg.f8.session.client;

import asia.cmg.f8.session.dto.BasicOrderInfo;
import asia.cmg.f8.session.dto.CreditPackageDTO;
import asia.cmg.f8.session.dto.FreeOrderRequest;
import asia.cmg.f8.session.entity.credit.CreditPackageType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Created on 11/28/16.
 */
@Component
public class FallbackCommerceClient implements FeignCommerceClient {
    @Override
    public List<BasicOrderInfo> getLatestOrderCommerce(final String userUuid, final String ptUuid, final Long latestTimestamp) {
        return Collections.emptyList();
    }

	@Override
	public Integer createFreeOrderMigrationUsers(final List<FreeOrderRequest> orders) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long subtractCreditWallet(String userUuid, int creditAmount, String transactionType, String description,
			List<String> descriptionParams) {
		return 0;
	}

	@Override
	public long plusCreditWallet(String userUuid, int creditAmount, String transactionType, String description,
			List<String> descriptionParams) {
		return 0;
	}

	@Override
	public long subtractCreditWallet(String userUuid, long bookingId, int creditAmount, String transactionType,
			String description, List<String> descriptionParams) {
		return 0;
	}

	@Override
	public long plusCreditWallet(String userUuid, long bookingId, int creditAmount, String transactionType,
			String description, List<String> descriptionParams) {
		return 0;
	}

	@Override
	public CreditPackageDTO getCreditPackageByType(CreditPackageType type) {
		return null;
	}
}

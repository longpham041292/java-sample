package asia.cmg.f8.schedule.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import asia.cmg.f8.common.dto.ApiRespObject;

import java.util.Collections;
import java.util.List;

public class CommerceClientFallback implements CommerceClient {

    private static final Logger LOG = LoggerFactory.getLogger(CommerceClientFallback.class);

    @Override
    public List<String> getPendingOrders(final int queryAfter) {
        LOG.info("Error when getting pending orders after minutes {}", queryAfter);
        return Collections.emptyList();
    }

    @Override
    public Boolean queryPaymentStatus(final String orderUuid) {
        LOG.info("Error when call to query payment for order {}", orderUuid);
        return null;
    }

	@Override
	public void checkExpiredPurchasedCreditPackage() {
		LOG.info("Error when calling to check expired purchased credit packages");
	}

	@Override
	public void autoWithdrawalUserCreditByWeeklyJob() {
		LOG.info("Error when calling auto withdrawal user credit by weekly");
	}

	@Override
	public void autoWithdrawalClubCreditByWeeklyJob() {
		LOG.info("Error when calling auto withdrawal club credit by weekly");
	}

	@Override
	public void expiredUserCreditPackageNotification() {
		LOG.info("Error when calling expired user credit package notification");
	}

}

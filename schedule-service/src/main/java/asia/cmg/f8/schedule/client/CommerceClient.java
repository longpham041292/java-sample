package asia.cmg.f8.schedule.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import asia.cmg.f8.common.dto.ApiRespObject;

import java.util.List;

@FeignClient(value = "commerce", url = "${service.commerceUrl}",
        fallback = CommerceClientFallback.class)
public interface CommerceClient {

    @RequestMapping(value = "/system/orders/pending?queryAfter={queryAfter}",
            method = RequestMethod.GET)
    List<String> getPendingOrders(@PathVariable("queryAfter") final int queryAfter);

    @RequestMapping(value = "/system/orders/{orderUuid}/query", method = RequestMethod.GET)
    Boolean queryPaymentStatus(@PathVariable("orderUuid") final String orderUuid);
    
    @RequestMapping(value = "/internal/v1/wallets/user-credit-packages/expired", method = RequestMethod.PUT)
    void checkExpiredPurchasedCreditPackage();
    
    @RequestMapping(value = "/internal/v1/credit/booking/users/auto-withdrawal", method = RequestMethod.POST)
    void autoWithdrawalUserCreditByWeeklyJob();
    
    @RequestMapping(value = "/internal/v1/credit/booking/clubs/auto-withdrawal", method = RequestMethod.POST)
    void autoWithdrawalClubCreditByWeeklyJob();
    
    @RequestMapping(value = "/internal/v1/wallets/credit-packages/expired/notification", method = RequestMethod.POST)
    void expiredUserCreditPackageNotification();
}

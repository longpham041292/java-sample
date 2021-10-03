package asia.cmg.f8.commerce.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "paymentQuery", url = "${service.onepay}")
public interface QueryPaymentClient {

    @RequestMapping(value = "/onecomm-pay/Vpcdps.op", method = RequestMethod.POST,
            consumes = "application/x-www-form-urlencoded")
    String domesticPostQueryData(@RequestBody final String data);
    
    @RequestMapping(value = "/vpcpay/Vpcdps.op", method = RequestMethod.POST,
            consumes = "application/x-www-form-urlencoded")
    String internationalPostQueryData(@RequestBody final String data);
}

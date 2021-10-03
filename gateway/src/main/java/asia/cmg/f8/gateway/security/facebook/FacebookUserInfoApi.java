package asia.cmg.f8.gateway.security.facebook;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import asia.cmg.f8.gateway.security.facebook.FbUserInfo;

/**
 * Created on 11/3/16.
 */
@FeignClient(name = "facebook", value = "facebook", url = "https://graph.facebook.com/v2.5")
public interface FacebookUserInfoApi {

    @RequestMapping(value = "/me?fields=id,name,email&access_token={access_token}", method = GET, produces = APPLICATION_JSON_VALUE)
    FbUserInfo getUserInfo(@PathVariable("access_token") final String token);
}

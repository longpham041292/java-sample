package asia.cmg.f8.user.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Add
 * Created on 10/24/16.
 */
@FeignClient(value = "facebook", url = "https://graph.facebook.com/v2.5")
public interface FacebookUserInfoClient {

    @RequestMapping(value = "/me", method = GET, produces = APPLICATION_JSON_VALUE)
    FbUserInfo getUserInfo(@RequestParam("access_token") final String token);

    class FbUserInfo {

        private String name;
        private String id;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }
    }
}

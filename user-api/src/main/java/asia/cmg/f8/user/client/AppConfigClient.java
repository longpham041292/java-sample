package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.AppConfigEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by nhieu on 8/4/17.
 */
@FeignClient(value = "users", url = "${feign.url}")
public interface AppConfigClient {

    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String QUERY_PARAM = "&ql=select name, value where group = 'mobile'";
    String GET_CONFIG = "/configs?" + SECRET_QUERY +QUERY_PARAM;

    @RequestMapping(method = GET, value = GET_CONFIG, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<AppConfigEntity> geConfigByQuery();


}

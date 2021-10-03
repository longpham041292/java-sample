package asia.cmg.f8.gateway.security.auth;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

import static asia.cmg.f8.gateway.SecurityUtil.AUTHORIZATION_HEADER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 11/1/16.
 */
@RestController
public class TestRest {

    @RequestMapping(value = "/testRest", method = GET)
    public Map<String, Object> get(@RequestHeader(AUTHORIZATION_HEADER) final String token) {
        return Collections.singletonMap(AUTHORIZATION_HEADER, token);
    }
}

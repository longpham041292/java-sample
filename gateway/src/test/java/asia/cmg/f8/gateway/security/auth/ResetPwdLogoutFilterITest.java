package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.test.SecurityITestAware;
import asia.cmg.f8.gateway.security.test.TestOauthToken;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 11/9/16.
 */
@ContextConfiguration(classes = ResetPwdLogoutFilterITest.TestResetPwdController.class)
public class ResetPwdLogoutFilterITest extends SecurityITestAware {

    @Test
    @TestOauthToken
    public void testResetPwd() throws Exception {
        request().perform(post("/testHello"))
                .andExpect(status().isOk());
    }

    @Configuration
    @RestController
    @SuppressWarnings("PMD")
    public static class TestResetPwdController {

        @RequestMapping(value = "/users/uuid/resetpassword/token", method = POST)
        public Map<String, Object> testResetPwd() {
            final Map<String, Object> data = new HashMap<>();
            data.put("data", Collections.singletonMap("uuid", UUID.randomUUID().toString()));
            return data;
        }

        @RequestMapping(value = "/testHello", method = POST)
        public Map<String, Object> testHello() {
            final Map<String, Object> data = new HashMap<>();
            data.put("data", Collections.singletonMap("uuid", UUID.randomUUID().toString()));
            return data;
        }
    }
}

package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.test.SecurityITestAware;
import asia.cmg.f8.gateway.security.test.TestOauthToken;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 11/1/16.
 */
@ContextConfiguration(classes = TestConfiguration.class)
public class SecuredRequestWrapperFilterITest extends SecurityITestAware {

    @Test
    @TestOauthToken
    public void testJwtTokenInjected() throws Exception {
        request().perform(get("/testRest"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bearer ")));
    }
}

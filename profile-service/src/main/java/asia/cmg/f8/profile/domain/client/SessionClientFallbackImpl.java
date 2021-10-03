package asia.cmg.f8.profile.domain.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collection;
import java.util.Set;


import asia.cmg.f8.common.spec.order.ImportUserResult;


/**
 * Created on 1/20/17.
 */
@Component
public class SessionClientFallbackImpl implements SessionClient {
    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFallbackImpl.class);

    @Override
    public Set<String> getContractingOfUser(@RequestParam("user_uuid") final String currentUserId,
                                            @RequestParam("user_role") final String userRole,
                                            @RequestParam("users") final Collection<String> users) {
        LOG.error("Could not get contracting user of user {}", currentUserId);
        return null;
    }
    
    @Override
    public ImportUserResult isValidUserCodeInMigrationDB(@RequestParam("userCode") final String  userCode, @RequestParam("userType") final String userType) {
    	LOG.error("Could not check userCode {}", userCode);
        return null;
    }

	
    
}

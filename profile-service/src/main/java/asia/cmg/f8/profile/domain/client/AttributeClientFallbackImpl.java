package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by nhieu on 8/31/17.
 */
@Component
public class AttributeClientFallbackImpl implements AttributeClient {
    private static final Logger LOG = LoggerFactory.getLogger(AttributeClientFallbackImpl.class);

//    @Override
//    public UserGridResponse<Attribute> getAttributes(
//            @PathVariable(value = "query") final String query,
//            @PathVariable(value = "limit") final int limit,
//            @PathVariable(value = "token") final String accessToken){
//        LOG.error("Could not get Attribute user of user with query {}", query);
//        return new UserGridResponse<>();
//    }
//
//    @Override
//    public UserGridResponse<Attribute> getAttributes(final int limit,final String accessToken) {
//        LOG.error("Could not get all Attributes ");
//        return new UserGridResponse<>();
//    }
    
    @Override
    public UserGridResponse<Attribute> getAttributes(final String query,
            final int limit) {
        LOG.error("Could not get all Attributes ");
        return new UserGridResponse<>();
    }
    
    @Override
    public UserGridResponse<Attribute> getAttributes(final int limit) {
        LOG.error("Could not get all Attributes ");
        return new UserGridResponse<>();
    }
}

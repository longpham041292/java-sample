package asia.cmg.f8.user.service;

import asia.cmg.f8.common.profile.SignUpUserEvent;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.user.client.RoleClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created on 11/17/16.
 */
@Component
public class RoleService {

    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);
    private static final String TRAINER_ROLE = "trainer";
    private static final String SUCCESS_MESSAGE = "Finished to assign user %s to trainer role";
    private static final String FAILED_MESSAGE = "Failed to assign user %s to trainer role";

    private final RoleClient roleClient;

    @Inject
    public RoleService(final RoleClient userClient) {
        this.roleClient = userClient;
    }

    public void processAssignTrainerRole(final SignUpUserEvent event) {

        if (event == null) {
            LOG.info("SignUpUserEvent message is empty");
            return;
        }

        if (!StringUtils.equalsIgnoreCase(event.getUserType().toString(), UserType.PT.toString())) {
            return;
        }

        LOG.info(String.format("Processing assign user %s to trainer role", event.getUserId().toString()));
        final boolean result = !roleClient.assignRole(event.getUserId().toString(), TRAINER_ROLE).getEntities().isEmpty();

        LOG.info(String.format(result ? SUCCESS_MESSAGE : FAILED_MESSAGE, event.getUserId().toString()));
    }
}

package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.profile.domain.client.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created on 11/30/16.
 */
@Service
public class ContractService {
    private static final Logger LOG = LoggerFactory.getLogger(ContractService.class);

    private final UserClient userClient;

    @Inject
    public ContractService(final UserClient userClient) {
        this.userClient = userClient;
    }

    public boolean createContractConnection(final OrderCompletedEvent event) {
        final String euUuid = event.getUserUuid().toString();
        final String ptUuid = event.getPtUuid().toString();
        if (userClient.createContractingConnection(ptUuid, euUuid).getEntities().isEmpty() ||
                userClient.createContractingConnection(euUuid, ptUuid).getEntities().isEmpty()) {
            LOG.error("Could not create Contracting Connection for user {} and user {} in order code {}", ptUuid, euUuid, event.getOrderCode().toString());
            return false;
        }
        LOG.info("Created Contracting Connection for the order code {}", event.getOrderCode().toString());
        return true;
    }
}

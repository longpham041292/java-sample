package asia.cmg.f8.notification.service.jira;

import asia.cmg.f8.notification.client.ServiceDeskClient;
import asia.cmg.f8.notification.dto.ServicedeskRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created on 12/29/16.
 */
@Service
public class JiraService {                                                  
    private final ServiceDeskClient serviceDeskClient;


    public JiraService(final ServiceDeskClient serviceDeskClient) {
        this.serviceDeskClient = serviceDeskClient;
    }

    public Optional<ResponseEntity> createCustomerRequest(final ServicedeskRequest request,
                                                          final String base64String) {
        return Optional.of(serviceDeskClient
                .postCustomerRequest(base64String, request));
    }


}

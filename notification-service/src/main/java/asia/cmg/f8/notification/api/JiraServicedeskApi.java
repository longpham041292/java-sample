package asia.cmg.f8.notification.api;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.config.JiraServiceDeskProperties;
import asia.cmg.f8.notification.dto.JiraTicketRequest;
import asia.cmg.f8.notification.service.jira.JiraService;
import asia.cmg.f8.notification.util.JiraServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

import static asia.cmg.f8.common.web.errorcode.ErrorCode.REQUEST_INVALID;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 12/29/16.
 */
@RestController
public class JiraServicedeskApi {
    private static final Logger LOG = LoggerFactory.getLogger(JiraServicedeskApi.class);
    private final JiraService jiraService;
    private final JiraServiceDeskProperties jiraProperties;
    private static final String SUCCESS = "success";

    public JiraServicedeskApi(final JiraService jiraService,
                              final JiraServiceDeskProperties jiraProperties) {
        this.jiraService = jiraService;
        this.jiraProperties = jiraProperties;
    }

    @RequestMapping(value = "/tickets", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createCustomerRequest(@Valid @RequestBody final JiraTicketRequest request,
                                                final BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            LOG.error("Error when validate signup data {}", bindingResult.getAllErrors());
            return new ResponseEntity<Object>(REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }
        final String base64String = JiraServiceUtils
                .base64ClientCredentials(jiraProperties.getUserName(),
                        jiraProperties.getPassword());

        final Optional<ResponseEntity> response = jiraService
                .createCustomerRequest(JiraServiceUtils.buildTicket(request,
                        Integer.valueOf(jiraProperties.getServiceDeskId()),
                        jiraProperties.getAnonymousUserRole(),
                        jiraProperties.getPtRole(),
                        jiraProperties.getEuRole())
                        , base64String);
        if (!response.get().getStatusCode().equals(HttpStatus.CREATED)) {
            LOG.warn("Create customer request fail");
            return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
    }
}

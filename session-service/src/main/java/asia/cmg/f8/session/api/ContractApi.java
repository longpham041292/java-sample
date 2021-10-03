package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.session.service.ContractingService;

/**
 * Created on 1/20/17.
 */
@RestController
public class ContractApi {

    private final ContractingService contractingService;

    public ContractApi(final ContractingService contractingService) {
        this.contractingService = contractingService;
    }

    @RequestMapping(value = "/session/users/contracting", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity checkContracting(@RequestParam("user_uuid") final String currentUserId,
                                           @RequestParam("user_role") final String userRole,
                                           @RequestParam("users") final List<String> users) {
        if (Objects.isNull(users) || users.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        return new ResponseEntity<>(contractingService
                .getContractingOfUser(currentUserId, userRole, users), HttpStatus.OK);
    }
}

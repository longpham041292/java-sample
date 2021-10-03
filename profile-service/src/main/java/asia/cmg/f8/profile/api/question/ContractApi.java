package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.profile.domain.entity.ContractUser;
import asia.cmg.f8.profile.domain.service.ContactService;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 11/21/16.
 */
@RestController
public class ContractApi {
    private static final String PROFILE_TAG_NAME = "Profile Management";

    private static final Logger LOG = LoggerFactory.getLogger(ContractApi.class);
    
    private final ContactService contactService;

    public ContractApi(final ContactService contactService) {
        this.contactService = contactService;
    }

    @ApiOperation(value = "Search PT's clients that have contract with current PT.", tags = PROFILE_TAG_NAME)
    @RequiredPTRole
    @RequestMapping(value = "/me/contracting", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<ContractUser> searchContractingOfPT(
            @RequestParam(value = "keyword", defaultValue = "") final String keyword,
            final Account account) {
       
    	LOG.info("------ >>>>>> ---------- ############## ");
    	
    	return contactService.searchContractingUserOfPT(keyword, account);
    }
}

package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.DocumentEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 10/31/16.
 */
@FeignClient(value = "documents", url = "${usergrid.baseUrl}", fallback = DocumentClientFallbackImpl.class)
public interface DocumentClient {
	String SECRECT_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
	
    @RequestMapping(value = "/documents?ql={query}&" + SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DocumentEntity> getDocumentsByQuery(@PathVariable("query") final String query);

    @RequestMapping(value = "/documents?" + SECRECT_QUERY, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DocumentEntity> storeDocuments(@RequestBody final DocumentEntity document);

    @RequestMapping(value = "/documents/{documentId}?" + SECRECT_QUERY, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DocumentEntity> deleteDocuments(@PathVariable(value = "documentId", required = true) final String documentId);

}

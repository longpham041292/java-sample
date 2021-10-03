package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.DocumentEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by on 11/1/16.
 */
@Component
public class DocumentClientFallbackImpl implements DocumentClient {
    @Override
    public UserGridResponse<DocumentEntity> getDocumentsByQuery(@PathVariable("query") final String query) {
        throw new UserGridException("ERROR_ON_QUERY_DOCUMENT", "Usergrid went wrong while getting document by query:" + query);
    }

    @Override
    public UserGridResponse<DocumentEntity> storeDocuments(@RequestBody final DocumentEntity document) {
        throw new UserGridException("ERROR_ON_STORE_DOCUMENT",
                "Usergrid went wrong while storing document uuid:" + document.getUuid());
    }

    @Override
    public UserGridResponse<DocumentEntity> deleteDocuments(@PathVariable(value = "documentId", required = true)
                                                            final String documentId) {
        throw new UserGridException("ERROR_ON_DELETE_DOCUMENT", "Usergrid went wrong while delete document uuid:" + documentId);
    }
}

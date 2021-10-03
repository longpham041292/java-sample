package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.client.DocumentClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.DocumentEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.entity.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by tri.bui on 10/25/16.
 */
@Component
public class DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);

    private static final String DOCUMENT_QUERY_OWNER = "select * where owner = '%s'";
    private static final String DOCUMENT_QUERY_EXIST = "select uuid where owner = '%s' and uuid = '%s'";
    private static final String USER_QUERY_DOCUMENT_STATUS = "select * where uuid = '%s'";

    @Autowired
    private DocumentClient documentClient;

    @Autowired
    private UserClient userClient;

    public List<DocumentEntity> getDocumentsByOwner(final String ownerId) {
        return documentClient.getDocumentsByQuery(String.format(DOCUMENT_QUERY_OWNER, ownerId)).getEntities();
    }

    public Optional<DocumentEntity> storeDocument(final DocumentEntity document, final String userId) {
        if (isApprovedDocument(userId)) {
            LOG.info("The approved document could not be added more");
            return Optional.empty();
        }
        return Optional.of(documentClient.storeDocuments(document).getEntities().get(0));
    }

    public Optional<DocumentEntity> deleteDocument(final String documentId, final String userId) {
        if (isApprovedDocument(userId)) {
            LOG.info("The approved document could not be deleted");
            return Optional.empty();
        }
        return Optional.of(documentClient.deleteDocuments(documentId).getEntities().get(0));
    }

    public boolean isNotExistingDocument(final String documentId, final String userId) {
        final UserGridResponse<DocumentEntity> documentExistResponse =
                documentClient.getDocumentsByQuery(String.format(DOCUMENT_QUERY_EXIST, userId, documentId));
        return documentExistResponse.getEntities().isEmpty();
    }

    private boolean isApprovedDocument(final String userId) {
        final UserGridResponse<UserEntity> userResponse = userClient.getUserByQuery(
                String.format(USER_QUERY_DOCUMENT_STATUS, userId));
        final UserStatus status = userResponse.getEntities().get(0).getStatus();
        return status != null && DocumentStatusType.APPROVED.equals(status.documentStatus());
    }
}

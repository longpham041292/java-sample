package asia.cmg.f8.profile.api.document;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.entity.DocumentEntity;
import asia.cmg.f8.profile.domain.service.DocumentService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by tri.bui on 10/17/16.
 */
@RestController
@ApiModel(description = "Document Management")
public class DocumentApi {
    private static final String DOCUMENT_TAG_DESCRIPTION = "Document Management";

    private final DocumentService documentService;
    private final UserProfileProperties userProfileProperties;

    @Autowired
    public DocumentApi(final DocumentService documentService, final UserProfileProperties userProfileProperties) {
        this.documentService = documentService;
        this.userProfileProperties = userProfileProperties;
    }

    @RequiredAdminRole
    @ApiOperation(value = "Admin gets list of PT's documents", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/{userId}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<DocumentResponse> getDocumentsOfUser(@PathVariable(value = "userId") final String userId) {
        return getDocumentsByUserId(userId);
    }

    @RequiredPTRole
    @ApiOperation(value = "Gets list of document of current user", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/me", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<DocumentResponse> getDocumentsOfCurrentUser(final Account account) {
        return getDocumentsByUserId(account.uuid());
    }

    @RequiredAdminRole
    @ApiOperation(value = "Admin adds document for PT. Used for Admin", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/{userId}", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addDocumentForUser(@PathVariable(value = "userId") final String userId,
                                             @RequestBody final SubmitDocumentRequest requestDocument) {
        return createDocumentOfUserId(requestDocument, userId);
    }

    @RequiredPTRole
    @ApiOperation(value = "PT adds document for approval. Used for PT", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/me", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity addDocumentForCurrentUser(@RequestBody final SubmitDocumentRequest requestDocument,
                                                    final Account account) {
        return createDocumentOfUserId(requestDocument, account.uuid());
    }

    @RequiredAdminRole
    @ApiOperation(value = "Admin deletes document of specific user. Used for Admin", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/{userId}/{documentId}", method = RequestMethod.DELETE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity deleteDocumentOfUser(
            @PathVariable(value = "userId", required = true) final String userId,
            @PathVariable(value = "documentId", required = true) final String documentId) {
        return deleteDocumentOfUserId(documentId, userId);
    }

    @RequiredPTRole
    @ApiOperation(value = "Delete document of current user. Used for PT", tags = DOCUMENT_TAG_DESCRIPTION)
    @RequestMapping(value = "/documents/me/{documentId}", method = RequestMethod.DELETE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity deleteDocumentOfCurrentUser(
            @PathVariable(value = "documentId", required = true) final String documentId,
            final Account account) {
        return deleteDocumentOfUserId(documentId, account.uuid());
    }

    private List<DocumentResponse> getDocumentsByUserId(final String userId) {
        return documentService.getDocumentsByOwner(userId)
                .stream()
                .map(documentEntity ->
                        documentEntity.getCreated() == null ? documentEntity : documentEntity.withDisplayCreated(DateUtils.formatDateTime(documentEntity.getCreated(), userProfileProperties.getDateTimeFormat())))
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
    }

    private ResponseEntity createDocumentOfUserId(final SubmitDocumentRequest requestDocument, final String userId) {
        final DocumentEntity document = DocumentEntity.builder()
                .path(requestDocument.getPath())
                .documentName(requestDocument.getDocumentName())
                .category(requestDocument.getCategory())
                .owner(userId)
                .build();

        final Optional<DocumentEntity> addedDocument = documentService.storeDocument(document, userId);

        if (!addedDocument.isPresent()) {
            return new ResponseEntity<Object>(ErrorCode.DOCUMENT_APPROVED_ALREADY, HttpStatus.BAD_REQUEST);
        }

        final DocumentEntity documentEntity = addedDocument.get();
        String displayCreated = "";
        if (documentEntity.getCreated() != null) {
            displayCreated = DateUtils.formatDateTime(documentEntity.getCreated(), userProfileProperties.getDateTimeFormat());
        }
        return new ResponseEntity<>(documentEntity.withDisplayCreated(displayCreated), HttpStatus.OK);
    }

    private ResponseEntity deleteDocumentOfUserId(final String documentId, final String userId) {
        if (documentService.isNotExistingDocument(documentId, userId)) {
            return new ResponseEntity<Object>(ErrorCode.DOCUMENT_NOT_EXISTING, HttpStatus.BAD_REQUEST);
        }

        final Optional<DocumentEntity> deletedDocument = documentService.deleteDocument(
                documentId, userId);
        return deletedDocument.isPresent()
                ? new ResponseEntity<>(deletedDocument.get(), HttpStatus.OK)
                : new ResponseEntity<Object>(ErrorCode.DOCUMENT_APPROVED_ALREADY, HttpStatus.BAD_REQUEST);
    }

}

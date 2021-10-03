package asia.cmg.f8.user.api;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.entity.ApproveDocumentResponse;
import asia.cmg.f8.user.entity.ApproveDocumentRequest;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.service.DocumentService;
import asia.cmg.f8.user.service.SubmitDocumentEmailCommand;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by on 11/2/16.
 */
@RestController
public class DocumentApi {

    private static final Logger LOG = Logger.getLogger(DocumentApi.class);
    private static final String SUCCESS = "success";
    private static final String MSG_COULD_NOT_FOUND_USER_ID = "Could not found user id: ";

    private final DocumentService documentService;
    private final UserProperties userProperties;

    public DocumentApi(final DocumentService documentService, final UserProperties userProperties) {
        this.documentService = documentService;
        this.userProperties = userProperties;
    }

    @RequiredPTRole
    @RequestMapping(value = "/documents/submit/me", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> submitDocumentsForPT(final Account account) {
        return submitDocumentByUserId(account.uuid());
    }

    @RequiredAdminRole
    @RequestMapping(value = "/documents/submit/{userId}", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    public Map<String, Object> submitDocumentsForAdmin(@PathVariable("userId") final String userId) {
        return submitDocumentByUserId(userId);
    }

    @RequiredAdminRole
    @RequestMapping(value = "/documents/approve", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ApproveDocumentResponse approveDocuments(@RequestBody final ApproveDocumentRequest body,
                                                    final Account account) {
        final Optional<UserEntity> documentResp = documentService.getUserWithDocumentStatus(body.getUserId());
        if (!documentResp.isPresent()) {
            LOG.info(MSG_COULD_NOT_FOUND_USER_ID + body.getUserId());
            return new ApproveDocumentResponse(Boolean.FALSE, null, null);
        }

        return documentService.approveDocument(body)
                .map(user -> new ApproveDocumentResponse(Boolean.TRUE,
                                user.getApprovedDate(),
                                DateUtils.formatDateTime(user.getApprovedDate(), userProperties.getDateTimeFormat())))
                .orElse(new ApproveDocumentResponse(Boolean.FALSE, null, null));
    }

    private Map<String, Object> submitDocumentByUserId(final String userId) {

        final Optional<UserEntity> documentResp = documentService.getUserWithDocumentStatus(userId);
        if (!documentResp.isPresent()) {
            LOG.info(MSG_COULD_NOT_FOUND_USER_ID + userId);
            return Collections.singletonMap(SUCCESS, Boolean.FALSE);
        }

        final UserEntity userEntity = documentResp.get();

        if (DocumentStatusType.APPROVED.equals(userEntity.getStatus().documentStatus())) {
            LOG.info("Could not submit Approved Documents for User uuid: " + userId);
            return Collections.singletonMap(SUCCESS, Boolean.FALSE);
        }

        if (!documentService.submitDocument(userId)) {
            LOG.info("Could not submit documents for User uuid: " + userId);
            return Collections.singletonMap(SUCCESS, Boolean.FALSE);
        }

        LOG.info("Documents have been submitted for User uuid: " + userId);
        documentService.process(new SubmitDocumentEmailCommand(userId));
        return Collections.singletonMap(SUCCESS, Boolean.TRUE);
    }
}

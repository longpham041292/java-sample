package asia.cmg.f8.user.service;

import asia.cmg.f8.common.event.email.SubmitDocumentAdminEvent;
import asia.cmg.f8.common.event.user.AdminApprovedDocumentEvent;
import asia.cmg.f8.common.event.user.UnapproveDocumentEvent;
import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.entity.ApproveDocumentRequest;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.entity.UserStatus;
import asia.cmg.f8.user.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by on 11/3/16.
 */
@Component
public class DocumentService {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentService.class);
    private static final String QUERY_STATUS_USER_BY_UUID = "select status where uuid = '%s'";

    public static final String TRAINER = CommonConstant.PT_USER_TYPE;

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private UserClient userClient;

    public void process(final SubmitDocumentEmailCommand command) {
        final SubmitDocumentAdminEvent event = SubmitDocumentAdminEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setUserId(command.getUserId())
                .setSubmittedAt(System.currentTimeMillis())
                .build();
        eventHandler.publish(event);
    }

    public Optional<UserEntity> approveDocument(final ApproveDocumentRequest requestApproval) {
        final DocumentStatusType docStatus = requestApproval.isApproved()
                ? DocumentStatusType.APPROVED
                : DocumentStatusType.ONBOARD;
        final String ptUuid = requestApproval.getUserId();
        final UserStatus newStatus = UserStatus.builder().documentStatus(docStatus).build();
        final UserEntity newUser = UserEntity.builder()
                .status(newStatus)
                .level(requestApproval.getLevelId())
                .approvedDate(System.currentTimeMillis())
                .approved(requestApproval.isApproved() ? true : false)
                .build();
        final List<UserEntity> result = userClient.approveDocument(ptUuid, newUser).getEntities();

        if (result.isEmpty()) { return Optional.empty(); }

        final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
                .setEventId(java.util.UUID.randomUUID().toString())
                .setUserId(requestApproval.getUserId())
                .setUserType(TRAINER)
                .setDocumentStatus(docStatus.name())
                .setLevel(requestApproval.getLevelId())
                .setSubmittedAt(System.currentTimeMillis())
                .build();

        LOG.info("Fired event for changing User Info: {}", ptUuid);
        eventHandler.publish(changeUserInfoEvent);

        //Fired event push notification to PT
        if (requestApproval.isApproved()) {
            final AdminApprovedDocumentEvent adminApprovedDocumentEvent =
                    new AdminApprovedDocumentEvent();
            adminApprovedDocumentEvent.setEventId(UUID.randomUUID().toString());
            adminApprovedDocumentEvent.setSubmittedAt(System.currentTimeMillis());
            adminApprovedDocumentEvent.setTrainerUuid(ptUuid);
            eventHandler.publish(adminApprovedDocumentEvent);
            LOG.info("Fired event for admin approved document of user {}", ptUuid);
        } else {
			final UnapproveDocumentEvent unapproveEvent = new UnapproveDocumentEvent();
			unapproveEvent.setEventId(UUID.randomUUID().toString());
			unapproveEvent.setSubmittedAt(System.currentTimeMillis());
			unapproveEvent.setTrainerUuid(ptUuid);
			eventHandler.publish(unapproveEvent);
			LOG.info("Fired event for admin unapproved document of trainer {}", ptUuid);
		}

        return Optional.of(result.get(0));
    }

    public boolean submitDocument(final String userId) {
        final UserStatus newStatus = UserStatus.builder().documentStatus(DocumentStatusType.PENDING).build();

        final UserGridResponse<UserEntity> updateDocumentStatusResp =
                userClient.submitDocument(userId, UserEntity.builder().status(newStatus).build());
        if (Objects.isNull(updateDocumentStatusResp) || updateDocumentStatusResp.isEmpty()) {
            LOG.info("Could not submit document for user: {}", userId);
            return false;
        }

        final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
                .setEventId(java.util.UUID.randomUUID().toString())
                .setUserId(userId)
                .setUserType(TRAINER)
                .setDocumentStatus(DocumentStatusType.PENDING.name())
                .setSubmittedAt(System.currentTimeMillis())
                .build();
        eventHandler.publish(changeUserInfoEvent);
        LOG.info("Fired event for changing User Info: {}", userId);

        return true;
    }

    public Optional<UserEntity> getUserWithDocumentStatus(final String userId) {
        final UserGridResponse<UserEntity> userResp = userClient.getUserByUUID(
                String.format(QUERY_STATUS_USER_BY_UUID, userId));
        return Optional.ofNullable(userResp.getEntities().isEmpty()
                ? null
                : userResp.getEntities().get(0));
    }
}

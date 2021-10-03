package asia.cmg.f8.notification.service.inbox;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.client.InboxMessageClient;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by on 1/3/17.
 */
@Service
public class InboxService {

    private static final Logger LOG = LoggerFactory.getLogger(InboxService.class);
    private static final String USER_GRID_ERR = "user-grid went wrong while executing query {}";
    private static final String QUERY_ALL_MESSAGE_OF_USER = "select * where user_id = '%s' order by created_date desc";
    private static final String QUERY_UNREAD_MESSAGE_OF_USER =
            "select * where user_id = '%s' and read =" + Boolean.FALSE + " order by created_date desc";
    private static final String QUERY_UUID_UNREAD_MESSAGE_OF_USER =
            "select * where user_id = '%s' and read =" + Boolean.FALSE;
    private static final String UPDATE_INBOX_OF_USER = "select * where uuid='%s' and user_id='%s'";

    private final InboxMessageClient inboxMessageClient;

    public InboxService(final InboxMessageClient inboxMessageClient) {
        this.inboxMessageClient = inboxMessageClient;
    }

    public Optional<PagedResponse<InboxMessageEntity>> getAllMessagesOfUser(
            final String userId,
            final int limit,
            final String cursor) {

        final String query = String.format(QUERY_ALL_MESSAGE_OF_USER, userId);
        final PagedResponse<InboxMessageEntity> messagesResp = inboxMessageClient
                .getMessagesByQueryWithPaging(query, limit, cursor);
        if (Objects.isNull(messagesResp)) {
            LOG.error(USER_GRID_ERR, query);
            return Optional.empty();
        }
        return Optional.of(messagesResp);
    }

    public Optional<PagedResponse<InboxMessageEntity>> getUnreadMessagesOfUser(
            final String userId,
            final int limit,
            final String cursor) {

        final String query = String.format(QUERY_UNREAD_MESSAGE_OF_USER, userId);
        final PagedResponse<InboxMessageEntity> messagesResp = inboxMessageClient
                .getMessagesByQueryWithPaging(query, limit, cursor);
        if (Objects.isNull(messagesResp)) {
            LOG.error(USER_GRID_ERR, query);
            return Optional.empty();
        }
        return Optional.of(messagesResp);
    }

    public Optional<InboxMessageEntity> updateMessageAsRead(final String messageId,
                                                            final Account account) {
        final UserGridResponse<InboxMessageEntity> messageResp = inboxMessageClient
                .updateMessages(String.format(UPDATE_INBOX_OF_USER, messageId, account.uuid()),
                        Collections.singletonMap("read", Boolean.TRUE));

        if (Objects.isNull(messageResp)) {
            LOG.error("Error while marking message {} as read", messageId);
            return Optional.empty();
        }

        return messageResp.getEntities().stream().findFirst();
    }

    public Integer getTotalUnreadMessagesOfUser(final String userId) {

        final String query = String.format(QUERY_UUID_UNREAD_MESSAGE_OF_USER, userId);
        final UserGridResponse<InboxMessageEntity> messagesResp = inboxMessageClient
                .getTotalUnreadMessage(query);
        if (Objects.isNull(messagesResp)) {
            LOG.error(USER_GRID_ERR, query);
            return null;
        }
        return messagesResp.getCount();
    }

    public Optional<InboxMessageEntity> createInboxMessage(final InboxMessageEntity entity) {
        final UserGridResponse<InboxMessageEntity> createdResp =
                inboxMessageClient.createInboxMessage(entity);
        if (Objects.isNull(createdResp) || createdResp.getEntities().isEmpty()) {
            LOG.error("Could not create Inbox Entity");
            return Optional.empty();
        }
        return createdResp.getEntities().stream().findFirst();
    }

    public Optional<List<InboxMessageEntity>> bulkCreateInboxMessage(final List<InboxMessageEntity> entity) {
        final UserGridResponse<InboxMessageEntity> createdResp =
                inboxMessageClient.createInboxMessage(entity);
        if (Objects.isNull(createdResp) || createdResp.getEntities().isEmpty()) {
            LOG.error("Could not create Inbox Entity");
            return Optional.empty();
        }
        return Optional.of(createdResp.getEntities());
    }
}

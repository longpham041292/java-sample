package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.PagedResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Created on 1/3/17.
 */
@Component
public class InboxMessageClientFallbackImpl implements InboxMessageClient {

    @Override
    public PagedResponse<InboxMessageEntity> getMessagesByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit,
            @RequestParam(value = CURSOR, required = false) final String cursor) {
        return null;
    }

    @Override
    public UserGridResponse<InboxMessageEntity> getTotalUnreadMessage(
            @PathVariable(QUERY) final String query) {
        return null;
    }

    @Override
    public UserGridResponse<InboxMessageEntity> updateMessages(
            @PathVariable(QUERY) final String query,
            @RequestBody final Map<String, Object> entity) {
        return null;
    }

    @Override
    public UserGridResponse<InboxMessageEntity> createInboxMessage(
            @RequestBody final InboxMessageEntity entity) {
        return null;
    }

    @Override
    public UserGridResponse<InboxMessageEntity> createInboxMessage(
            @RequestBody final List<InboxMessageEntity> entity) {
        return null;
    }
}

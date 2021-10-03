package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.PagedResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 1/3/17.
 */

@FeignClient(value = "inboxMessages", url = "${feign.url}",
        fallback = InboxMessageClientFallbackImpl.class)
public interface InboxMessageClient {
    String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
    String QUERY = "query";
    String LIMIT = "limit";
    String CURSOR = "cursor";
    String INBOX_MESSAGES_URL_PREFIX = "/inbox_messages?";

    String CREATE_INBOX_MESSAGE = INBOX_MESSAGES_URL_PREFIX + SECRET_QUERY;
    String QUERY_INBOX_MESSAGE = INBOX_MESSAGES_URL_PREFIX + SECRET_QUERY + "&ql={query}&limit={limit}&cursor={cursor}";
    String QUERY_TOTAL_UNREAD_MESSAGE = INBOX_MESSAGES_URL_PREFIX + SECRET_QUERY
            + "&ql={query}&limit=${notification.maxSearchResult}";
    String UPDATE_INBOX_MESSAGE = INBOX_MESSAGES_URL_PREFIX + SECRET_QUERY + "&ql={query}";

    @RequestMapping(value = QUERY_INBOX_MESSAGE,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    PagedResponse<InboxMessageEntity> getMessagesByQueryWithPaging(
            @PathVariable(QUERY) final String query,
            @RequestParam(LIMIT) final int limit,
            @RequestParam(value = CURSOR, required = false) final String cursor);

    @RequestMapping(value = QUERY_TOTAL_UNREAD_MESSAGE,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<InboxMessageEntity> getTotalUnreadMessage(
            @PathVariable(QUERY) final String query);

    @RequestMapping(value = UPDATE_INBOX_MESSAGE,
            method = RequestMethod.PUT,
            produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<InboxMessageEntity> updateMessages(
            @PathVariable(QUERY) final String query,
            @RequestBody Map<String, Object> entity);

    @RequestMapping(value = CREATE_INBOX_MESSAGE,
            method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE,
            consumes = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<InboxMessageEntity> createInboxMessage(@RequestBody InboxMessageEntity entity);

    @RequestMapping(value = CREATE_INBOX_MESSAGE,
            method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE,
            consumes = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<InboxMessageEntity> createInboxMessage(@RequestBody List<InboxMessageEntity> entity);
}

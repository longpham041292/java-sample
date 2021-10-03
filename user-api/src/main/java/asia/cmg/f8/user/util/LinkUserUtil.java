package asia.cmg.f8.user.util;

import asia.cmg.f8.user.dto.UserAuthResponse;
import asia.cmg.f8.user.entity.LinkUserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created on 1/10/17.
 */
public final class LinkUserUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(LinkUserUtil.class);

    private LinkUserUtil() {
        // empty
    }

    /**
     * A convenience method to build link's name.
     *
     * @param linkingUser the uuid of linking user
     * @param linkedUser  the uuid of linked user
     * @return the link's name
     */
    public static String buildLinkName(final String linkingUser, final String linkedUser) {
        return linkingUser + "_linked_" + linkedUser;
    }

    /**
     * A convenience method to build {@link LinkUserEntity}.
     *
     * @param linkingUser the uuid of linking user
     * @param linkedUser  the uuid of linked user
     * @return new {@link LinkUserEntity}.
     */
    public static LinkUserEntity create(final String linkingUser, final String linkedUser) {
        final LinkUserEntity entity = new LinkUserEntity();
        entity.setLinkedUser(linkedUser);
        entity.setLinkingUser(linkingUser);
        entity.setName(buildLinkName(linkingUser, linkedUser));
        return entity;
    }

    public static String buildDeleteQuery(final List<LinkUserEntity> entities) {
        final StringJoiner query = new StringJoiner(" or ");
        entities.forEach(link -> query.add("name='" + link.getName() + "'"));
        return "where " + query;
    }

    public static Map<String, Object> buildAuthBody(final String user, final String pwd) {
        final Map<String, Object> body = new HashMap<>(3);
        body.put("grant_type", "password");
        body.put("username", user);
        body.put("password", pwd);

        LOGGER.info("Built request token for user {}", user);

        return body;
    }

    public static boolean isValid(final UserAuthResponse response) {
        return response.getAccessToken() != null;
    }

    public static String getUserUuid(final UserAuthResponse response) {
        final Map<String, Object> user = response.getUser();
        if (user != null) {
            return (String) user.get("uuid");
        }
        return null;
    }
}

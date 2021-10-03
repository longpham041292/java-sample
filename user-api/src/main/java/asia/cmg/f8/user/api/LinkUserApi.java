package asia.cmg.f8.user.api;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.client.LinkUserClient;
import asia.cmg.f8.user.dto.LinkUser;
import asia.cmg.f8.user.dto.LinkUserRequest;
import asia.cmg.f8.user.dto.UserAuthResponse;
import asia.cmg.f8.user.dto.UserInfoResponse;
import asia.cmg.f8.user.entity.LinkUserEntity;
import asia.cmg.f8.user.exception.LinkUserException;
import asia.cmg.f8.user.util.LinkUserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import static asia.cmg.f8.common.web.errorcode.ErrorCode.INTERNAL_SERVICE_ERROR;
import static asia.cmg.f8.common.web.errorcode.ErrorCode.REQUEST_INVALID;
import static asia.cmg.f8.user.util.LinkUserUtil.buildAuthBody;
import static asia.cmg.f8.user.util.LinkUserUtil.getUserUuid;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 1/10/17.
 */
@RestController
public class LinkUserApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(LinkUserApi.class);

    @SuppressWarnings("squid:S2068")
    public static final String INVALID_USRNAME_OR_PWD = "INVALID_USERNAME_OR_PASSWORD";

    public static final String LINKING_EXISTED = "LINKING_EXISTED";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String INVALID_FACEBOOK_ID = "INVALID_FACEBOOK_ID";
    public static final String CAN_NOT_LINK_TO_PT = "CAN_NOT_LINK_TO_PT";

    private final LinkUserClient linkUserClient;

    public LinkUserApi(final LinkUserClient linkUserClient) {
        this.linkUserClient = linkUserClient;
    }

    @RequestMapping(method = GET, path = "/linkUser", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity loadLinkedUser(final Account account) {

        List<LinkUserEntity> entities = Collections.emptyList();
        final UserGridResponse<LinkUserEntity> response = linkUserClient.findByQuery("select * where linked_user=" + account.uuid());
        if (response != null && response.getEntities() != null && !response.getEntities().isEmpty()) {
            entities = response.getEntities();
        }
        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @RequestMapping(method = DELETE, value = "/linkUser", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity unLinkUser(final Account account) {

        if (account == null) {
            return new ResponseEntity<>(REQUEST_INVALID.withDetail("Failed to inject current logged-in user"), BAD_REQUEST);
        }

        final UserGridResponse<LinkUserEntity> response = linkUserClient.findByQuery("select * where linking_user=" + account.uuid() + " or linked_user=" + account.uuid());

        if (response.getEntities() == null || response.getEntities().isEmpty()) {
            return new ResponseEntity<>(REQUEST_INVALID.withDetail(account.uuid() + " has not linked with any user yet."), BAD_REQUEST);
        }

        final String query = LinkUserUtil.buildDeleteQuery(response.getEntities());

        final UserGridResponse<LinkUserEntity> unLink = linkUserClient.unLink(query);

        LOGGER.info("Unlinked user using query <{}>", query);

        return new ResponseEntity<>(unLink.getEntities(), OK);
    }

    @RequestMapping(method = POST, value = "/linkUser", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity linkUser(@RequestBody final LinkUserRequest linkUserRequest, final Account account) {

        if (!isValid(linkUserRequest)) {
            return new ResponseEntity<>(REQUEST_INVALID, OK);
        }

        final LinkUser linkingUser = prepareForLink(linkUserRequest.getLinkingFbId(), linkUserRequest.getLinkingEmail(), linkUserRequest.getLinkingPwd());
        final LinkUser linkedUser = prepareForLink(linkUserRequest.getLinkedFbId(), linkUserRequest.getLinkedEmail(), linkUserRequest.getLinkedPwd());

        // validation.
        validateIfValidToLink(account, linkingUser, linkedUser);

        final String linkingUuid = linkingUser.getUuid();
        final String linkedUuid = linkedUser.getUuid();

        final List<LinkUserEntity> entities = new ArrayList<>(2);
        entities.add(LinkUserUtil.create(linkingUuid, linkedUuid));
        entities.add(LinkUserUtil.create(linkedUuid, linkingUuid));

        final UserGridResponse<LinkUserEntity> response = linkUserClient.createLink(entities);
        final List<LinkUserEntity> linkedEntities = response.getEntities();
        if (linkedEntities.isEmpty()) {
            return new ResponseEntity<>(INTERNAL_SERVICE_ERROR.withDetail("Something wrong when link user " + linkingUuid + " and " + linkedUuid), BAD_REQUEST);
        }

        LOGGER.info("Linked {} and {}", linkingUuid, linkedUuid);

        return new ResponseEntity<>(linkedEntities, OK);
    }

    /**
     * A convenience method to validate {@link LinkUser}s before actually linking.
     * {@link LinkUserException} is thrown the validation failed.
     *
     * @param account     the current logged in account
     * @param linkingUser the linking user
     * @param linkedUser  the linked user.
     */
    private void validateIfValidToLink(final Account account, final LinkUser linkingUser, final LinkUser linkedUser) {

        /**
         * Do not allow to link a PT with a PT user.
         */
        if (linkingUser.isPt() && linkedUser.isPt()) {
            throw new LinkUserException(REQUEST_INVALID.withError(CAN_NOT_LINK_TO_PT, "Failed to link 2 PT users"));
        }

        if (!account.uuid().equals(linkingUser.getUuid())) {
            throw new LinkUserException(REQUEST_INVALID.withDetail("Linking user does not match with current logged-in user. Does not allow to link"));
        }

        final String linkingUuid = linkingUser.getUuid();
        final String linkedUuid = linkedUser.getUuid();

        if (hasLinkedUser(linkingUuid)) {
            throw new LinkUserException(REQUEST_INVALID.withError(LINKING_EXISTED, linkingUuid + " is linked with another user"));
        }

        if (hasLinkedUser(linkedUuid)) {
            throw new LinkUserException(REQUEST_INVALID.withError(LINKING_EXISTED, linkedUuid + " is linked with another user"));
        }
    }

    /**
     * A convenience method to validate data before actually link this user.
     *
     * @param facebookId the facebook id. It's optional
     * @param email      the user's email.
     * @param pwd        the user's pwd.
     * @return a valid {@link LinkUser} which is used for linking user.
     */
    private LinkUser prepareForLink(final String facebookId, final String email, final String pwd) {

        final String uuid;
        final UserType userType;

        if (facebookId != null && facebookId.length() > 0) {
            final Optional<UserInfoResponse> linkingUser = getUserFromFacebookId(facebookId);
            if (!linkingUser.isPresent()) {
                throw new LinkUserException(REQUEST_INVALID.withError(INVALID_FACEBOOK_ID, "There is no registered user with this facebook id or user is deactivated"));
            }
            final UserInfoResponse userInfoResponse = linkingUser.get();
            uuid = userInfoResponse.getUuid();
            userType = userInfoResponse.getUserType();
        } else {

            final String linkingUserName = getUserNameByEmail(email);
            if (linkingUserName == null) {
                throw new LinkUserException(REQUEST_INVALID.withError(INVALID_EMAIL, "Invalid email"));
            }

            final UserAuthResponse linkingToken = doAuth(linkingUserName, pwd);
            if (linkingToken == null) {
                throw new LinkUserException(REQUEST_INVALID.withError(INVALID_USRNAME_OR_PWD, "Failed to auth user " + email));
            }
            uuid = getUserUuid(linkingToken);
            userType = linkingToken.getUserType();
        }
        return new LinkUser(uuid, userType);
    }

    @SuppressWarnings("squid:S2221")
    private UserAuthResponse doAuth(final String user, final String pwd) {
        try {
            return linkUserClient.doAuth(buildAuthBody(user, pwd));
        } catch (final Exception e) {
            LOGGER.warn("Linking account, failed to authenticate user " + user, e);
            return null;
        }
    }

    private boolean isValid(final LinkUserRequest request) {
        final String linkingFbId = request.getLinkingFbId();
        final String linkedFbId = request.getLinkedFbId();
        // does not allow to link 2 facebook users.
        return !(linkedFbId != null && linkingFbId != null) && !Objects.equals(request.getLinkingEmail(), request.getLinkedEmail());
    }

    private Optional<UserInfoResponse> getUserFromFacebookId(final String fbId) {
        final UserGridResponse<UserInfoResponse> response = linkUserClient.findUserByQuery("select uuid,username,userType where activated=true and facebook.id='" + fbId + "'");
        if (response != null && response.getEntities() != null) {
            return response.getEntities().stream()
                    .filter(facebook -> !facebook.getUsername().startsWith("fb_"))
                    .findFirst();
        }
        return Optional.empty();
    }

    private boolean hasLinkedUser(final String uuid) {

        final StringJoiner query = new StringJoiner(" or ");
        query.add("linking_user=" + uuid);
        query.add("linked_user=" + uuid);

        final UserGridResponse<LinkUserEntity> response = linkUserClient.findByQuery(query.toString());
        return response != null && response.getEntities() != null && !response.getEntities().isEmpty();
    }


    private String getUserNameByEmail(final String email) {
        final UserGridResponse<UserInfoResponse> response = linkUserClient.findUserByQuery("select uuid,username,userType where email='" + email + "'");
        if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
            return null;
        }
        return response.getEntities().iterator().next().getUsername();
    }
}

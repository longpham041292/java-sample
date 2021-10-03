package asia.cmg.f8.profile.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.profile.FollowingConnectionEvent;
import asia.cmg.f8.common.profile.UserUnFollowingConnectionEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.social.FollowingAction;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.ContractUser;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.event.EventHandler;

/**
 * Created on 11/14/16.
 */
@Service
public class ContactService {
    private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);
    private static final String QUERY_CONTRACTING_USER_BY_KEYWORD =
            "select uuid,username,name,picture"
                    + " where (username = '*%1$s*' or name = '*%1$s*') and (not uuid = '%2$s') and activated = true"
                    + " order by username asc, name asc";
    private static final String QUERY_ALL_CONTRACTING_USER =
            "select uuid,username,name,picture"
                    + " where (not uuid = '%2$s') and activated = true"
                    + " order by username asc, name asc";

    private final EventHandler eventHandler;
    private final UserClient userClient;

    @Inject
    public ContactService(final EventHandler eventHandler,
                          final UserClient userClient) {
        this.eventHandler = eventHandler;
        this.userClient = userClient;
    }

    public void fireFollowingEvent(
            final String userId,
            final String followerId,
            final FollowingAction action,
            final UserEntity userInfo) {
        final FollowingConnectionEvent followingConnectionEvent = new FollowingConnectionEvent().newBuilder()
                .setEventId(java.util.UUID.randomUUID().toString())
                .setUserId(userId)
                .setFollowerId(followerId)
                .setAction(action.toString())
                .setSubmittedAt(System.currentTimeMillis())
                .setUsername(userInfo.getUsername())
                .build();

        LOG.info(String.format("Fire following connection event for user %1$s to user %2$s with action %3$s",
                userId, followerId, action.toString()));

        eventHandler.publish(followingConnectionEvent);
    }

    public List<ContractUser> searchContractingUserOfPT(final String keyword, final Account account) {
        final List<UserEntity> contractingUserEntity = userClient.searchContractingUser(account.uuid(),
                String.format(StringUtils.isBlank(keyword)
                        ? QUERY_ALL_CONTRACTING_USER
                        : QUERY_CONTRACTING_USER_BY_KEYWORD, keyword, account.uuid())).getEntities();

        final List<ContractUser> contractUsers = contractingUserEntity.stream()
                .map(user -> ContractUser.builder().userUuid(user.getUuid())
                        .name(user.getName())
                        .picture(user.getPicture())
                        .username(user.getUsername()).build())
                .collect(Collectors.toList());
        return contractUsers;
    }
    
    public void fireUnFollowingEvent(
            final String userId,
            final String followerId,
            final FollowingAction action,
            final UserEntity userInfo) {
        final UserUnFollowingConnectionEvent unFollowingConnectionEvent = new UserUnFollowingConnectionEvent().newBuilder()
                .setEventId(java.util.UUID.randomUUID().toString())
                .setUserId(userId)
                .setFollowerId(followerId)
                .setAction(action.toString())
                .setSubmittedAt(System.currentTimeMillis())
                .setUsername(userInfo.getUsername())
                .build();

        LOG.info(String.format("Fire following connection event for user %1$s to user %2$s with action %3$s",
                userId, followerId, action.toString()));

        eventHandler.publish(unFollowingConnectionEvent);
    }
}

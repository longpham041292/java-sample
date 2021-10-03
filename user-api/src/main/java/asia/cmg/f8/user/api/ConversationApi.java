package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.ParticipantImpl;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.service.ConversationService;

/**
 * Created by on 11/8/16.
 */

@RestController
public class ConversationApi {
    private final static Logger LOG = LoggerFactory.getLogger(ConversationApi.class);
    private final static String USER_ID_PROPERTY = "userId";

    private final ConversationService conversationService;

    public ConversationApi(final ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @RequestMapping(value = "/conversations", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    public PagedUserResponse<ParticipantImpl> getMyConversations(
            final Account account,
            @RequestParam(value = "cursor", required = false) final String cursor,
            @RequestParam(value = "limit", defaultValue = "10") final int limit) {
        final PagedUserResponse<UserEntity> userResponse =
                conversationService.getMyConversations(account, cursor, limit);
        final PagedUserResponse<ParticipantImpl> result = new PagedUserResponse<>();
        result.setCursor(userResponse.getCursor());
       // result.setEntities(userResponse.getEntities().stream().map(entity ->parseUserEntityToParticipantLastMsg(entity,account.uuid(),account.ugAccessToken())).collect(Collectors.toList()));
       result.setEntities(userResponse.getEntities().stream().map(this::parseUserEntityToParticipant).collect(Collectors.toList()));
        return result;
    }

    @RequestMapping(value = "/conversations", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity createConversation(@RequestBody final Map<String, String> body,
                                             final Account account) {
        final String partnerId = body.get(USER_ID_PROPERTY);
        if (conversationService.isNotValidUser(account.uuid(), partnerId)) {
            LOG.info("Could not found user uuid: " + partnerId);
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> userResp =
                conversationService.createConversations(account, partnerId);
        if (userResp.isPresent()) {
            return new ResponseEntity<>(parseUserEntityToParticipant(userResp.get()), HttpStatus.OK);
        }

        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/conversations/{userId}", method = RequestMethod.DELETE,
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity deleteConversationByUserId(
            @PathVariable(value = "userId", required = true) final String userId,
            final Account account) {
        if (conversationService.isNotValidUser(account.uuid(), userId)) {
            LOG.info("Could not found user uuid: " + userId);
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> userResp =
                conversationService.deleteConversation(account, userId);
        if (userResp.isPresent()) {
            return new ResponseEntity<>(parseUserEntityToParticipant(userResp.get()), HttpStatus.OK);
        }

        return new ResponseEntity<>(ErrorCode.CONVERSATION_NOT_EXIST, HttpStatus.BAD_REQUEST);
    }

    private ParticipantImpl parseUserEntityToParticipant(final UserEntity userEntity) {
        return ParticipantImpl.builder()
                .uuid(userEntity.getUuid())
                .picture(Objects.isNull(userEntity.getPicture())
                        ? StringUtils.EMPTY : userEntity.getPicture())
                .name(userEntity.getName())
                .lastMsg(userEntity.getLastMsg())
                .lastMsgOwner(userEntity.getLastMsgOwner())
                .lastMsgReceiver(userEntity.getLastMsgReceiver())
                .lastMsgType(userEntity.getLastMsgType())
                .modified(userEntity.getModified())
                .lastMsgTime(userEntity.getLastMsgTime())
                .lastMsgId(userEntity.getLastMsgId())
                .build();
    }
    

    
}
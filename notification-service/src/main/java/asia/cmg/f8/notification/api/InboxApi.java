package asia.cmg.f8.notification.api;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.dto.InboxMessage;
import asia.cmg.f8.notification.dto.InboxSender;
import asia.cmg.f8.notification.dto.OptionResponse;
import asia.cmg.f8.notification.dto.QuestionResponse;
import asia.cmg.f8.notification.entity.InboxMessageEntity;
import asia.cmg.f8.notification.entity.InboxMessageType;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.entity.Profile;
import asia.cmg.f8.notification.entity.QuestionEntity;
import asia.cmg.f8.notification.entity.UserEntityImpl;
import asia.cmg.f8.notification.service.inbox.InboxService;
import asia.cmg.f8.notification.service.inbox.QuestionAnswerService;
import asia.cmg.f8.notification.service.inbox.SessionService;
import asia.cmg.f8.notification.service.inbox.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 1/3/17.
 */
@RestController
@SuppressWarnings("PMD.ExcessiveImports")
public class InboxApi {

    private static final Logger LOG = LoggerFactory.getLogger(InboxApi.class);

    private final InboxService inboxService;
    private final UserService userService;
    private final SessionService sessionService;
    private final QuestionAnswerService questionAnswerService;

    public InboxApi(final InboxService inboxService,
                    final UserService userService,
                    final SessionService sessionService,
                    final QuestionAnswerService questionAnswerService) {
        this.inboxService = inboxService;
        this.userService = userService;
        this.sessionService = sessionService;
        this.questionAnswerService = questionAnswerService;
    }

    @RequiredPTRole
    @RequestMapping(value = "/inbox/messages", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public PagedResponse<InboxMessage> getInboxMessages(
            final Account account,
            @RequestParam(value = "filter_name") final String filterName,
            @RequestParam(value = "limit", defaultValue = "10") final int limit,
            @RequestParam(value = "cursor", required = false) final String cursor) {
        if ("all".equals(filterName)) {
            final Optional<PagedResponse<InboxMessageEntity>> messageResp = inboxService.getAllMessagesOfUser(account.uuid(), limit, cursor);
            if (!messageResp.isPresent() || messageResp.get().getEntities().isEmpty()) {
                return new PagedResponse<>();
            }
            return populateDataInboxMessage(messageResp.get());
        } else if ("unread".equals(filterName)) {
            final Optional<PagedResponse<InboxMessageEntity>> messageResp =
                    inboxService.getUnreadMessagesOfUser(account.uuid(), limit, cursor);
            if (!messageResp.isPresent() || messageResp.get().getEntities().isEmpty()) {
                return new PagedResponse<>();
            }
            return populateDataInboxMessage(messageResp.get());
        }
        return new PagedResponse<>();
    }

    @RequiredPTRole
    @RequestMapping(value = "/inbox/messages/{uuid}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public Observable<ResponseEntity> getMessageById(final Account account,
                                                     @PathVariable("uuid") final String messageId,
                                                     final LanguageContext language) {

        //update inbox message as read
        final Optional<InboxMessageEntity> messageResp = inboxService.updateMessageAsRead(messageId, account);
        if (!messageResp.isPresent()) {
            return Observable.just(new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST));
        }

        final InboxMessageEntity inboxMessageEntity = messageResp.get();
        final String senderId = inboxMessageEntity.getSenderId();

        return Observable.zip(
                userService.getUserInformationByUuidObservable(Collections.singleton(senderId)),
                sessionService.getSessionPackageById(inboxMessageEntity.getPackageId()),
                (userEntityMap, packageEntity) -> {
                    final Map<String, Object> messageContent = inboxMessageEntity.getContent();

                    //If message type is NewClient, load show question answer to return response
                    if (InboxMessageType.NEW_CLIENT.equals(inboxMessageEntity.getInboxMessageType())) {

                        final List<QuestionResponse> listUserQuestion = inboxMessageEntity.getClientInfo();

                        final Map<String, QuestionResponse> userQuestionMap;
                        if (listUserQuestion == null || listUserQuestion.isEmpty()) {
                            userQuestionMap = Collections.emptyMap();
                        } else {
                            userQuestionMap = new HashMap<>(listUserQuestion.size());
                            for (final QuestionResponse response : listUserQuestion) {
                                userQuestionMap.putIfAbsent(response.getKey(), response);
                            }
                        }

                        final List<QuestionEntity> shownQuestionResp = questionAnswerService.getAdminShowQuestions(account, language.language());
                        final List<QuestionResponse> listShownContent = shownQuestionResp.stream()
                                .map(questionEntity -> {
                                    final QuestionResponse response = new QuestionResponse(questionEntity);
                                    if (userQuestionMap.containsKey(questionEntity.getKey())) {
                                        final Set<String> userChoose = userQuestionMap.get(questionEntity.getKey()).getOptions().stream()
                                                .filter(OptionResponse::isChoose)
                                                .map(OptionResponse::getKey).collect(Collectors.toSet());

                                        response.setAnswered(true);
                                        response.setOptions(
                                                response.getOptions().stream().map(optionResponse -> {
                                                    if (userChoose.contains(optionResponse.getKey())) {
                                                        optionResponse.setChoose(Boolean.TRUE);
                                                    }
                                                    return optionResponse;
                                                }).collect(Collectors.toList()));
                                    }
                                    return response;
                                }).collect(Collectors.toList());

                        return new ResponseEntity<>(InboxMessage.builder()
                                .id(inboxMessageEntity.getId())
                                .messageType(inboxMessageEntity.getInboxMessageType())
                                .messageContent(Collections.emptyMap())
                                .createdDate(inboxMessageEntity.getCreatedDate())
                                .isRead(inboxMessageEntity.isRead())
                                .sessionBurned(packageEntity.getSessionBurned())
                                .totalSession(packageEntity.getTotalSession())
                                .sender(convertUserEntityToSender(userEntityMap.get(senderId)))
                                .clientInfo(listShownContent)
                                .build(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity(InboxMessage.builder()
                                .id(inboxMessageEntity.getId())
                                .messageType(inboxMessageEntity.getInboxMessageType())
                                .messageContent(messageContent)
                                .createdDate(inboxMessageEntity.getCreatedDate())
                                .isRead(inboxMessageEntity.isRead())
                                .sessionBurned(packageEntity.getSessionBurned())
                                .totalSession(packageEntity.getTotalSession())
                                .sender(convertUserEntityToSender(userEntityMap.get(senderId)))
                                .clientInfo(Collections.emptyList())
                                .build(), HttpStatus.OK);
                    }
                })
                .doOnError(error -> LOG.error("Something went wrong while populating data message detail error {}", error))
                .firstOrDefault(new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST));
    }

    @RequiredPTRole
    @RequestMapping(value = "/inbox/messages/unread/total", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Integer> getMessageById(final Account account) {
        return Collections.singletonMap("total", inboxService.getTotalUnreadMessagesOfUser(account.uuid()));
    }

    /**
     * Populate data for Inbox Message DTO.
     *
     * @param pagedResponse paged response
     * @return Inbox Message DTO
     */
    private PagedResponse<InboxMessage> populateDataInboxMessage(final PagedResponse<InboxMessageEntity> pagedResponse) {

        final Set<String> listUserId = pagedResponse
                .getEntities()
                .stream()
                .map(InboxMessageEntity::getSenderId)
                .collect(Collectors.toSet());

        //get user information request
        final Map<String, UserEntityImpl> userEntityMap = userService.getUserInformationByUuid(listUserId);

        final PagedResponse<InboxMessage> result = new PagedResponse<>();
        result.setCursor(pagedResponse.getCursor());
        result.setEntities(pagedResponse
                .getEntities()
                .stream()
                .map(entity -> InboxMessage
                        .builder()
                        .description(EMPTY)
                        .createdDate(entity.getCreatedDate())
                        .isRead(entity.isRead())
                        .id(entity.getId())
                        .messageType(entity.getInboxMessageType())
                        .sender(convertUserEntityToSender(userEntityMap.get(entity.getSenderId())))
                        .build())
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Convert UserEntity to InboxSender DTO.
     *
     * @param userEntity user entity
     * @return InboxSender DTO
     */
    private InboxSender convertUserEntityToSender(final UserEntityImpl userEntity) {

        String senderId = EMPTY;
        String senderName = EMPTY;
        String picture = EMPTY;
        String phone = EMPTY;

        if (!Objects.isNull(userEntity)) {
            senderId = Optional.ofNullable(userEntity.getUuid()).orElse(EMPTY);
            senderName = Optional.ofNullable(userEntity.getName()).orElse(EMPTY);
            picture = Optional.ofNullable(userEntity.getPicture()).orElse(EMPTY);
            phone = Optional.ofNullable(userEntity.getProfile()).map(Profile::getPhone).orElse(EMPTY);
        }
        return InboxSender
                .builder()
                .id(senderId)
                .fullName(senderName)
                .picture(picture)
                .phone(phone)
                .build();
    }
}

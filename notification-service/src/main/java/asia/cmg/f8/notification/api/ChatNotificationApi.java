package asia.cmg.f8.notification.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.notification.dto.ChatNotificationRequest;
import asia.cmg.f8.notification.push.NotificationService;

/**
 * Created by nhieu on 8/9/17.
 */
@RestController
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class ChatNotificationApi {

    private final NotificationService chatNotificationService;
    private static final String EU_UUID = "4d3f87c3-a35b-11e7-a0e8-02420a010404";   // look into usergrid to find the uuid
    private static final String PT_UUID = "59b14341-a35b-11e7-a0e8-02420a010404";   // look into usergrid to find the uuid

    public ChatNotificationApi(final NotificationService chatNotificationService){
        this.chatNotificationService = chatNotificationService;
    }
    @PostMapping(value = "/notifications/users/{uuid}")
    public ResponseEntity<Void> sendToUser(final Account account, @PathVariable("uuid") final String uuid,
                                          @RequestBody final ChatNotificationRequest request) {

        chatNotificationService.sendToUser(account, uuid, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping(value = "/notifications/{userType}/users")
    public ResponseEntity<Void> sendToUserType(@PathVariable("userType") final String userType,
                                          @RequestBody final ChatNotificationRequest request) {
        final String groupUuid = userType.trim().equalsIgnoreCase("pt") ? PT_UUID : EU_UUID;
        chatNotificationService.sendToUserGroup(groupUuid, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping(value = "notifications/kate/{uuid}")
    public ResponseEntity<Void> sendToAllUser(final Account account, @PathVariable("uuid") final String uuid,
                                          @RequestBody final ChatNotificationRequest request) {

        chatNotificationService.getAllUser(account);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

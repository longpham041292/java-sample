package asia.cmg.f8.user.api;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.client.SessionClient;
import asia.cmg.f8.user.domain.client.AdminChangeNameRequest;
import asia.cmg.f8.user.domain.client.AdminChangeUserCodeRequest;
import asia.cmg.f8.user.domain.client.AdminChangeUserNameRequest;
import asia.cmg.f8.user.domain.client.ChangePasswordRequest;
import asia.cmg.f8.user.domain.client.ChangeUsernameRequest;
import asia.cmg.f8.user.domain.client.ResetUserPasswordRequest;
import asia.cmg.f8.user.dto.ResetUserPassword;
import asia.cmg.f8.user.entity.ChangePassword;
import asia.cmg.f8.user.entity.ChangeUsername;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.service.UserManagementService;
import asia.cmg.f8.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created on 1/4/17.
 */
@RestController
public class ChangeUsernamePasswordApi {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeUsernamePasswordApi.class);
    private static final String SUCCESS = "success";
    private static final int USER_CODE_VALID = 0;

    private final UserService userService;
    private final UserManagementService userManagementService;
    private final SessionClient sessionClient;

    public ChangeUsernamePasswordApi(final UserService userService, final UserManagementService userManagementService ,final SessionClient sessionClient) {
        this.userService = userService;
        this.userManagementService = userManagementService;
        this.sessionClient = sessionClient;
    }

    @RequestMapping(value = "/changePassword", method = PUT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changePassword(final Account account, @RequestBody final ChangePasswordRequest request) {
        if (!userService.changePassword(account.uuid(), ChangePassword
                .builder()
                .oldPassword(request.getOldPassword())
                .newPassword(request.getNewPassword())
                .build(), account.ugAccessToken())) {
            LOG.warn("Old password is not correct");
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID
                    .withError("INVALID_OLD_PASSWORD", "Old password is not correct"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/admin/change-user-password/", method = PUT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity resetUserPassword(final Account account, @RequestBody final ResetUserPasswordRequest request) {
    	ResetUserPassword newPassword = new ResetUserPassword();
    	newPassword.setNewPassword(request.getPassword());
        if (!userService.resetUserPassword(request.getUuid(), newPassword)) {
            LOG.warn("Can not change password");
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID
                    .withError("INVALID_CHANGE_PASSWORD", "Can not change password"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
    }

    @RequestMapping(value = "/changeUsername", method = PUT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity changeUsername(final Account account, @RequestBody final ChangeUsernameRequest request) {
        final String newUsername = request.getNewUsername();

        if (newUsername == null || newUsername.isEmpty()) {
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> user = userManagementService.findByUserName(newUsername);
        if (user.isPresent()) {
            LOG.warn("Username {} already exist", newUsername);
            return new ResponseEntity<>(ErrorCode.ENTRY_EXIST, HttpStatus.BAD_REQUEST);
        }

        final ChangeUsername changeUserNameRequest = ChangeUsername
                .builder()
                .newUsername(newUsername).build();

        final boolean success = userService.changeUsername(account.uuid(), changeUserNameRequest, account);

        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, success), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/adminChangeUsernameForUser", method = PUT, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public ResponseEntity changeUsernameForUser(final Account account, @RequestBody final AdminChangeUserNameRequest request) {
        final String newUsername = request.getNewUsername();

        if (StringUtils.isEmpty(request.getNewUsername()) || StringUtils.isEmpty(request.getUuid())) {
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> user = userManagementService.findByUserName(newUsername);
        if (user.isPresent()) {
            LOG.warn("Username {} already exist", newUsername);
            return new ResponseEntity<>(ErrorCode.ENTRY_EXIST, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> currentUser = userManagementService.findByQuery("where uuid='" + request.getUuid() + "'");
        
        if (!currentUser.isPresent()) {
            LOG.warn("User uuid {} is not existed", request.getUuid() );
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }

        final boolean success = userService.adminChangeUsername(request, account.ugAccessToken());

        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, success), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/adminChangeUsercodeForUser", method = PUT, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public ResponseEntity changeUserCodeForUser(final Account account, @RequestBody final AdminChangeUserCodeRequest request) {
        final String newUserCode = request.getNewUserCode();
        String userType = null;
        String oldUserCode = null;
        String oldUserLevel = null;

        if (StringUtils.isEmpty(request.getNewUserCode()) ) {
            return new ResponseEntity<>(ErrorCode.INVALID_USER_CODE, HttpStatus.BAD_REQUEST);
        }
        if ( StringUtils.isEmpty(request.getUuid())) {
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }
        
      
        if (userManagementService.isUserCodeExisted(newUserCode)) {
            LOG.warn("UserCode {} already exist", newUserCode);
            return new ResponseEntity<>(ErrorCode.DUPLICATE_USER_CODE, HttpStatus.BAD_REQUEST);
        }
        
       final Optional<UserEntity> currentUser = userManagementService.findByQuery("where uuid='" + request.getUuid() + "'");
        
        if (!currentUser.isPresent()) {
            LOG.warn("User uuid {} is not existed", request.getUuid() );
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }
        else
        {
        	userType = currentUser.get().getUserType().toString().toLowerCase();
        	oldUserCode = currentUser.get().getUsercode();
        	oldUserLevel = currentUser.get().getLevel();
        }
        
        final ImportUserResult validUserCode = sessionClient.isValidUserCodeInMigrationDB(request.getNewUserCode(), userType);
        
        if(USER_CODE_VALID != validUserCode.getErrorCode()) {
        	return new ResponseEntity<>(ErrorCode.INVALID_USER_CODE, HttpStatus.BAD_REQUEST);	
        }
        
        final boolean success = userService.adminChangeUserCode(request, account.ugAccessToken(), userType, oldUserLevel, validUserCode.getLevel());
        
        LOG.info("----- adminChangeUserCodeForUser ----- ");
        return new ResponseEntity<>(Collections.singletonMap(success, true), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/adminChangeNameForUser", method = PUT, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public ResponseEntity changeNameForUser(final Account account, @RequestBody final AdminChangeNameRequest request) {

        if (StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getUuid())) {
            return new ResponseEntity<>(ErrorCode.REQUEST_INVALID, HttpStatus.BAD_REQUEST);
        }

        final Optional<UserEntity> currentUser = userManagementService.findByQuery("where uuid='" + request.getUuid() + "'");
        
        if (!currentUser.isPresent()) {
            LOG.warn("User uuid {} is not existed", request.getUuid() );
            return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST, HttpStatus.BAD_REQUEST);
        }

        final boolean success = userService.adminChangeName(request, account.ugAccessToken());

        return new ResponseEntity<>(Collections.singletonMap(SUCCESS, success), HttpStatus.OK);
    }
    
}

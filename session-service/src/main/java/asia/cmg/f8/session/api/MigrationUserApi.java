/**
 * 
 */
package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.service.MigrationUserService;
import asia.cmg.f8.common.spec.order.ImportUserResult;
/**
 * @author khoa.bui
 *
 */
@RestController
public class MigrationUserApi {

	private final MigrationUserService migrationUserService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationUserApi.class);
	
	@Inject
	public MigrationUserApi(final MigrationUserService migrationUserService ) {
		this.migrationUserService = migrationUserService;
	}

	@RequestMapping(value = "/session/users/migrationExisted", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ImportUserResult isValidUserCodeInMigrationDB(@RequestParam("userCode") final String userCode, @RequestParam("userType") final String userType) {
		LOGGER.info("------------------Invoking /session/users/migrationExisted with userCode: " + userCode);
		if (StringUtils.isEmpty(userCode)) {
			final ImportUserResult result = new ImportUserResult();
			result.setErrorCode(ErrorCode.INVALID_USER_CODE.getCode());
			return result;
		}

		return migrationUserService.validUserCodeInSystem(userCode, userType);
	}
	
}

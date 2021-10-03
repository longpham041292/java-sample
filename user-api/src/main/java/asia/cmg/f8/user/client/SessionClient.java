/**
 * 
 */
package asia.cmg.f8.user.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
/**
 * @author khoa.bui
 *
 */
@FeignClient(value = "session", url = "${service.sessionUrl}")
public interface SessionClient {

	@RequestMapping(value = "/session/users/migrationExisted", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	ImportUserResult isValidUserCodeInMigrationDB(@RequestParam("userCode") final String  userCode, @RequestParam("userType") final String userType);
}

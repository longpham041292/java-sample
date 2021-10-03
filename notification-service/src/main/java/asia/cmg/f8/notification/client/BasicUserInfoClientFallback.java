package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created on 1/7/17.
 */
@Component
public class BasicUserInfoClientFallback implements BasicUserInfoClient {

    @Override
    public UserGridResponse<BasicUserInfo> findBasicUserInfo(@PathVariable("uuid") final String userUuid) {
        return null;
    }

    @Override
    public UserGridResponse<BasicUserInfo> findBasicUserInfoByQuery(
            @PathVariable("query") final String query) {
        return null;
    }

	@Override
	public PagedResponse<BasicUserInfo> searchUsersWithCursor(final String query, final int limit, final String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

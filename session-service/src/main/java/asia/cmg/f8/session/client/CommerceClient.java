package asia.cmg.f8.session.client;

import asia.cmg.f8.session.dto.BasicOrderInfo;

import java.util.List;

/**
 * Created on 11/28/16.
 */
public interface CommerceClient {

    List<BasicOrderInfo> getLatestOrderCommerce(final String userUuid, final String ptUuid, final Long latestTimestamp);

}

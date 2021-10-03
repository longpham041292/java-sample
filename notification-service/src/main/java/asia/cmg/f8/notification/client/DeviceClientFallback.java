package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.DeviceInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Created on 1/6/17.
 */
@Component
public class DeviceClientFallback implements DeviceClient {

    @Override
    public UserGridResponse<Map<String, Object>> connectDevice(@PathVariable("user_uuid") final String userUuid, @PathVariable("devices_uuid") final String deviceUuid) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> unConnectDevice(@PathVariable("user_uuid") final String userUuid, @PathVariable("devices_uuid") final String deviceUuid) {
        return null;
    }

    @Override
    public UserGridResponse<DeviceInfo> findDevices(@PathVariable("uuid") final String userUuid) {
        return null;
    }

	

//	@Override
//	public UserGridResponse<Map<String, Object>> deleteDublicateDivice(final String query, final String token) {
//		return null;
//	}
//
//	@Override
//	public UserGridResponse<DeviceInfo> findDevicesApsNotifierId(final String query, final String token, final int limit, final String cursor) {
//		return null;
//	}

	@Override
	public UserGridResponse<Map<String, Object>> searchDevices(final String query) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

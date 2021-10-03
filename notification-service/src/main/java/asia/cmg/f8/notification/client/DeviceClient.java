package asia.cmg.f8.notification.client;


import asia.cmg.f8.notification.dto.DeviceInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created on 1/6/17.
 */
@FeignClient(name = "devices", url = "${feign.url}", fallback = DeviceClientFallback.class)
public interface DeviceClient {

	String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
	String LIMIT = "limit";
	String QUERY_PARAM = "query";
	
    @RequestMapping(method = POST, path = "/users/{user_uuid}/devices/{devices_uuid}?" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Map<String, Object>> connectDevice(@PathVariable("user_uuid") final String userUuid, @PathVariable("devices_uuid") final String deviceUuid);

    @RequestMapping(method = DELETE, path = "/users/{user_uuid}/devices/{devices_uuid}?" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<Map<String, Object>> unConnectDevice(@PathVariable("user_uuid") final String userUuid, @PathVariable("devices_uuid") final String deviceUuid);

    @RequestMapping(method = GET, path = "/users/{uuid}/devices?" + SECRET_QUERY, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<DeviceInfo> findDevices(@PathVariable("uuid") final String userUuid);
    
//    @RequestMapping(method = GET, path = "/devices?access_token={token}&limit={limit}&cursor={cursor}&query={query}", produces = APPLICATION_JSON_UTF8_VALUE)
//   UserGridResponse<DeviceInfo> findDevicesApsNotifierId(@PathVariable(QUERY_PARAM) final String query, @PathVariable("token") final String token,@PathVariable("limit") final int limit, @PathVariable("cursor") final String cursor);
//    
//   @RequestMapping(method = DELETE, path = "/devices?access_token={token}&query={query}", produces = APPLICATION_JSON_UTF8_VALUE)
//    UserGridResponse<Map<String, Object>> deleteDublicateDivice(@PathVariable(QUERY_PARAM) final String query, @PathVariable("token") final String token);
   
   @RequestMapping(value = "/devices?"+ SECRET_QUERY + "&ql={query}",method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
   UserGridResponse<Map<String, Object>> searchDevices(@PathVariable(QUERY_PARAM) final String query); 
  
    
}

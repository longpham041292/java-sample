package asia.cmg.f8.user.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.common.dto.GroupEntity;

@FeignClient(value = "groups", url = "${feign.url}")
public interface GroupClient {
	String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
	String QUERY_LIMIT = "&limit={limit}";
	String QUERY_CURSOR = "&cursor={cursor}";
	String QUERY_QUERY = "&ql={query}";
	String GET_GROUP = "/groups?" + SECRET_QUERY + QUERY_LIMIT + QUERY_QUERY;
	String GET_GROUP_CURSOR = "/groups?" + SECRET_QUERY + QUERY_LIMIT + QUERY_CURSOR + QUERY_QUERY;
	String GET_USER = "/groups/{group}/users?" + SECRET_QUERY + QUERY_LIMIT + QUERY_QUERY;
	String GET_USER_CURSOR = "/groups/{group}/users?" + SECRET_QUERY + QUERY_LIMIT + QUERY_CURSOR + QUERY_QUERY;
	String CREATE_GROUP = "/groups?" + SECRET_QUERY;
	String UPDATE_GROUP = "/groups/{uuid}?" + SECRET_QUERY;
	String DELETE_GROUP = "/groups/{uuid}?" + SECRET_QUERY;
	String ASSIGN_GROUP = "/groups/{group}/users/{uuid}?" + SECRET_QUERY;
	String LIMIT = "limit";
	String CURSOR = "cursor";
	String QUERY = "query";
	String GROUP = "group";
	String UUID = "uuid";

    @RequestMapping(value = GET_GROUP, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<GroupEntity> getGroupsByQuery(@RequestParam(LIMIT) final int limit, @RequestParam(value = QUERY, required = false) final String query);
    
    @RequestMapping(value = GET_GROUP_CURSOR, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<GroupEntity> getGroupsByQuery(@RequestParam(LIMIT) final int limit, @RequestParam(value = QUERY, required = false) final String query, @RequestParam(CURSOR) final String cursor);
    
    @RequestMapping(value = GET_USER, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUsersByQuery(@PathVariable(GROUP) final String group, @RequestParam(LIMIT) final int limit, @RequestParam(value = QUERY, required = false) final String query);

    @RequestMapping(value = GET_USER_CURSOR, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getUsersByQuery(@PathVariable(GROUP) final String group, @RequestParam(LIMIT) final int limit, @RequestParam(value = QUERY, required = false) final String query, @RequestParam(value = CURSOR, required = false) final String cursor);
    
    @RequestMapping(value = CREATE_GROUP, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<GroupEntity> createGroup(@RequestBody final GroupEntity group);
    
    @RequestMapping(value = UPDATE_GROUP, method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<GroupEntity> updateGroup(@PathVariable(UUID) final String uuid, @RequestBody final GroupEntity group);
    
    @RequestMapping(value = DELETE_GROUP, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<GroupEntity> deleteGroup(@PathVariable(UUID) final String uuid);
    
    @RequestMapping(value = ASSIGN_GROUP, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<GroupEntity> assignToGroup(@PathVariable(UUID) final String uuid,
                                            @PathVariable(GROUP) final String group);
    
    @RequestMapping(value = ASSIGN_GROUP, method = RequestMethod.DELETE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<GroupEntity> removeFromGroup(@PathVariable(UUID) final String uuid,
                                            @PathVariable(GROUP) final String group);
}

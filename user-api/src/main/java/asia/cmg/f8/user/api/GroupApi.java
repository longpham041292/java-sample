package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.GroupEntity;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.service.GroupService;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

@RestController
public class GroupApi {
	private static final Logger LOG = LoggerFactory.getLogger(GroupApi.class);
	
	final static String KEYWORD = "keyword";
	final static String LIMIT = "limit";
    final static String CURSOR = "cursor";
	final static String SUCCESS = "success";
	private static final String SORT_PATTERN = " order by %s";
	
	@Autowired
    private GroupService groupService;
	
	@Inject
    private UserProperties userProperties;

    @RequestMapping(value = "/groups", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public PagedUserResponse<GroupEntity> getGroups(@RequestParam(value = KEYWORD, required = false) final String keyword,
            @PageableDefault(size = 1000, direction = Sort.Direction.ASC, sort = {"title"}) final Pageable pageable,
            @RequestParam(value = CURSOR, required = false) final String cursor) {
    	
    	int size = pageable.getPageSize() > userProperties.getAdmin().getMaxLoad() ? userProperties.getAdmin().getMaxLoad()
                : pageable.getPageSize();
        size = size < 1 ? userProperties.getAdmin().getUserLoad() : pageable.getPageSize();

        final Order order = pageable.getSort().iterator().next();
        final String sort = order.getProperty() + " " + order.getDirection();
        final String sortCondition = String.format(SORT_PATTERN, sort);
        
        final StringBuilder query = new StringBuilder("select *");
        if (StringUtils.isNotEmpty(keyword)) {
            query.append(String.format(" where (path = '*%s*' or title = '*%s*')", keyword, keyword));
        }
  
        query.append(sortCondition);
        
        return groupService.getGroups(size, query.toString(), cursor);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups/{group}/users", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public PagedUserResponse<UserEntity> getUsers(@PathVariable("group") final String group,
    		@RequestParam(value = KEYWORD, required = false) final String keyword,
            @PageableDefault(size = 50, direction = Sort.Direction.ASC, sort = {"name"}) final Pageable pageable,
            @RequestParam(value = CURSOR, required = false) final String cursor) {
    	
    	int size = pageable.getPageSize() > userProperties.getAdmin().getMaxLoad() ? userProperties.getAdmin().getMaxLoad()
                : pageable.getPageSize();
        size = size < 1 ? userProperties.getAdmin().getUserLoad() : pageable.getPageSize();

        final Order order = pageable.getSort().iterator().next();
        final String sort = order.getProperty() + " " + order.getDirection();
        final String sortCondition = String.format(SORT_PATTERN, sort);
        
        final StringBuilder query = new StringBuilder("select *");
        if (StringUtils.isNotEmpty(keyword)) {
            query.append(String.format(" where (username = '*%s*' or name = '*%s*')", keyword, keyword));
        }
  
        query.append(sortCondition);
        return groupService.getUsers(group, size, query.toString(), cursor);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createGroup(@RequestBody final GroupEntity group) {
    	LOG.info("Create Group: {} - {}", group.getPath(),group.getTitle());
    	final Optional<GroupEntity> result = groupService.createGroup(group);
    	if (result.isPresent()) {
            return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups/{uuid}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateGroup(@PathVariable("uuid") final String uuid, @RequestBody final GroupEntity group) {
    	LOG.info("Update Group: {} - {}", group.getPath(),group.getTitle());
    	final Optional<GroupEntity> result = groupService.updateGroup(uuid, group);
    	if (result.isPresent()) {
            return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups/{uuid}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteGroup(@PathVariable("uuid") final String uuid) {
    	final Optional<GroupEntity> result = groupService.deleteGroup(uuid);
    	if (result.isPresent()) {
            return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups/{group}/{uuid}/assign", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity assignToGroup(@PathVariable("uuid") final String uuid,
                                            @PathVariable("group") final String group) {
    	final Optional<GroupEntity> result = groupService.assignToGroup(uuid, group);
    	if (result.isPresent()) {
            return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }
    
    @RequiredAdminRole
    @RequestMapping(value = "/groups/{group}/{uuid}/remove", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeFromGroup(@PathVariable("uuid") final String uuid,
                                            @PathVariable("group") final String group) { 
    	final Optional<GroupEntity> result = groupService.removeFromGroup(uuid, group);
        if (result.isPresent()) {
            return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.BAD_REQUEST);
    }

}

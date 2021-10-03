package asia.cmg.f8.user.service;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import asia.cmg.f8.user.client.GroupClient;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.common.dto.GroupEntity;
import asia.cmg.f8.common.util.UserGridResponse;

@Service
public class GroupService {
	private static final Logger LOG = LoggerFactory.getLogger(GroupService.class);
	
	@Autowired
    private GroupClient groupClient;

    public PagedUserResponse<GroupEntity> getGroups(final int limit, final String query, final String cursor) {
    	if(StringUtils.isNotEmpty(cursor)) {
    		return groupClient.getGroupsByQuery(limit, query, cursor);
    	} else {
    		return groupClient.getGroupsByQuery(limit, query);
    	}
    }
    
    public PagedUserResponse<UserEntity> getUsers(final String group, final int limit, final String query, final String cursor) {
    	if(StringUtils.isNotEmpty(cursor)) {
    		return groupClient.getUsersByQuery(group, limit, query, cursor);
    	} else {
    		return groupClient.getUsersByQuery(group, limit, query);
    	}
    }
    
    public Optional<GroupEntity> createGroup(final GroupEntity group) {
    	 
    	final UserGridResponse<GroupEntity> result = groupClient.createGroup(group);
        if (Objects.isNull(result)
                || result.getEntities().isEmpty()) {
            LOG.info("Usergrid could not create group: {}", group.getPath());
            return Optional.empty();
        }
        return result.getEntities().stream().findFirst();
    }
    
    public Optional<GroupEntity> updateGroup(final String uuid, final GroupEntity group) {
   	 
    	final UserGridResponse<GroupEntity> result = groupClient.updateGroup(uuid, group);
        if (Objects.isNull(result)
                || result.getEntities().isEmpty()) {
            LOG.info("Usergrid could not update group: {}", uuid);
            return Optional.empty();
        }
        return result.getEntities().stream().findFirst();
    }
    
    public Optional<GroupEntity> deleteGroup(final String uuid) {
    	 
    	final UserGridResponse<GroupEntity> result = groupClient.deleteGroup(uuid);
        if (Objects.isNull(result)
                || result.getEntities().isEmpty()) {
            LOG.info("Usergrid could not delete group: {}", uuid);
            return Optional.empty();
        }
        return result.getEntities().stream().findFirst();
    }
    
    public Optional<GroupEntity> assignToGroup(@PathVariable("uuid") final String uuid,
                                            @PathVariable("group") final String group) {
    	 
    	final UserGridResponse<GroupEntity> result = groupClient.assignToGroup(uuid, group);
        if (Objects.isNull(result)
                || result.getEntities().isEmpty()) {
            LOG.info("Usergrid could not assignToGroup: {} - {}", uuid, group);
            return Optional.empty();
        }
        return result.getEntities().stream().findFirst();
    }
    
    public Optional<GroupEntity> removeFromGroup(@PathVariable("uuid") final String uuid,
                                            @PathVariable("group") final String group) {
    	 
    	final UserGridResponse<GroupEntity> result = groupClient.removeFromGroup(uuid, group);
        if (Objects.isNull(result)
                || result.getEntities().isEmpty()) {
        	LOG.info("Usergrid could not removeFromGroup: {} - {}", uuid, group);
            return Optional.empty();
        }
        return result.getEntities().stream().findFirst();
    }
}

package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.client.UserProfileClient;
import asia.cmg.f8.user.config.UserProperties;
import asia.cmg.f8.user.domain.client.PTUserReportInfo;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;

@RestController
public class UserManagementApi {
	private static final Logger LOG = LoggerFactory.getLogger(UserManagementApi.class);
	
    @Inject
    private UserClient userClient;
    
    @Inject
    private UserProperties userProperties;

    @Inject
    private PagedResourcesAssembler<PTUserReportInfo> pagedResourcesAssembler;
    
    @Inject
    private UserProfileClient userProfileClient;
    
    private static final String GET_PT_USER = "select * where userType = 'pt'";
    private static final String SORT_PATTERN = " order by %s";
    private static final String GET_AVATAR_QUERY = "select uuid where picture contains '*www.gravatar.com*'";
    private static final int DEFAULT_LIMIT = 1_000;
    private static final String SUCCESS = "success";
    
    @RequestMapping(value = "/admin/users/pt", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public PagedResources<Resource<PTUserReportInfo>> getListPt(
            @RequestParam(required = false) final String keyword,
            @PageableDefault(size = 10, direction = Sort.Direction.ASC, sort = {"name"}) final Pageable pageable,
            @RequestParam(value = "cursor", required = false) final String cursor,
            @RequestParam(value = "active", required = false) final boolean active,
            @RequestParam(value = "city", required = false) final String city,
            final Account account) throws UnsupportedEncodingException {

        int size;
        size = pageable.getPageSize() > userProperties.getAdmin().getMaxLoad() ? userProperties.getAdmin().getMaxLoad()
                : pageable.getPageSize();
        size = size < 1 ? userProperties.getAdmin().getUserLoad() : pageable.getPageSize();

        final Order order = pageable.getSort().iterator().next();
        final String sort = order.getProperty() + " " + order.getDirection();
        final String sortCondition = String.format(SORT_PATTERN, sort);
        
        final StringBuilder query = new StringBuilder(GET_PT_USER);
        if (StringUtils.isNotEmpty(keyword)) {
            query.append(String.format(" and (username = '*%s*' or name = '*%s*')", keyword, keyword));
        }
        if(active){
            query.append(String.format(" and activated = true and status.document_status='%s'",
                    DocumentStatusType.APPROVED.name().toLowerCase()));
        }
        if(StringUtils.isNotEmpty(city)) {
        	query.append(String.format(" and profile.city='%s'",city));
        }
        
        query.append(sortCondition);

        final PagedUserResponse<UserEntity> response;
        if (StringUtils.isEmpty(cursor)) {
            response = userClient.searchUserWithPaging(query.toString(), size);
        } else {
            response = userClient.searchUserWithPaging(query.toString(), size, cursor);
        }

        final String sortParam = order.getProperty() + "," + order.getDirection();
        final String cursorUri = ControllerLinkBuilder.linkTo(UserManagementApi.class).toString()
                + "?size=" + size + "&cursor=" + response.getCursor()
                + "&sort=" + sortParam
                + (!StringUtils.isEmpty(keyword) ? "&keyword=" + keyword : "")
                + (active ? "&active=true" : "");
        final Link link = new Link(URLEncoder.encode(cursorUri, "UTF-8"));
        return pagedResourcesAssembler.toResource(
                new PageImpl<>(response.getEntities().stream().filter(pt -> pt.getProfile() != null)
                        .map(PTUserReportInfo::new)
                        .collect(Collectors.toList())), link);
    }
    
    @RequestMapping(value = "/admin/users/avatar", method = RequestMethod.PUT, produces = APPLICATION_JSON_VALUE)
    @RequiredAdminRole
    public Map<String, Object> processUpdatingDefaultImage(@RequestBody final Map<String, Object> body) {
    	final Optional<String> avatarURL = Optional.ofNullable((String) body.get("avatarURL"));

        if (!avatarURL.isPresent()) {
            throw new IllegalArgumentException("avatarURL is required for this action.");
        }
    	try {
			String cursor = null;
			PagedUserResponse<UserEntity> response = userProfileClient.getOldDefaultImageUser(GET_AVATAR_QUERY,
					DEFAULT_LIMIT);
			do {

				if (null != response) {
					if (null != response.getEntities()) {
						final List<UserEntity> entities = response.getEntities();
						for (final UserEntity item : entities) {
							final UserEntity newUser = UserEntity.builder()
									.picture(avatarURL.get()).build();
							// this will wait until the result come then process
							// the next item.
							userProfileClient.updateOldAvartaImage(item.getUuid(), newUser);
							LOG.info("Update default image user {}", item.getUuid());

							// don't stress the user-grid so sleep a little
							// bit??
							try {
								TimeUnit.MILLISECONDS.sleep(200L);
							} catch (final InterruptedException exception) {
								// just leave it here
							}
						}
					}

					cursor = response.getCursor();
					LOG.info("Fetching users with old default image pageSize {} cursor {}", DEFAULT_LIMIT, cursor);
					if (null != cursor) {
						response = userProfileClient.getOldDefaultImageUserWithCursor(GET_AVATAR_QUERY,
								DEFAULT_LIMIT, cursor);
					}

				}
			} while (null != cursor && response != null);
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return Collections.singletonMap(SUCCESS, Boolean.FALSE);
		}
		return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}
}

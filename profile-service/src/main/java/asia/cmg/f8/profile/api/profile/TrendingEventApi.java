package asia.cmg.f8.profile.api.profile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;
import asia.cmg.f8.profile.domain.service.TrendingNewsEventService;
import asia.cmg.f8.profile.dto.PageResponse;
import asia.cmg.f8.profile.dto.TrendingEventDTO;

@RestController
public class TrendingEventApi {

	@Autowired
	TrendingNewsEventService trendingEventService;
	
	@GetMapping(value = "/admin/v1/trending_events/search", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getAllTrending(@RequestParam(name = "start_time") long startTime,
												@RequestParam(name = "end_time") long endTime,
												@RequestParam(name = "keyword", required = false) String keyword,
												@RequestParam(name = "activated") boolean activated, 
												final Pageable pageable, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime),
					TimeZone.getDefault().toZoneId());
			final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime),
					TimeZone.getDefault().toZoneId());
			PageResponse<TrendingEventEntity> pagedResponse = trendingEventService.searchTrendingEventsByKeyword(start, end, keyword, activated, pageable);
			
			apiResponse.setData(pagedResponse);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/admin/v1/trending_event", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> createTrendingEvent(@RequestBody final TrendingEventDTO trendingRequestData, final Account account) {
		
		ApiRespObject<TrendingEventEntity> apiResponse = new ApiRespObject<TrendingEventEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			TrendingEventActionEntity trendingAction = trendingEventService.getTrendingActionById(trendingRequestData.getAction());
			HomeSectionEntity homeSection = trendingEventService.getHomeSectionById(trendingRequestData.getSection());
			
			if(homeSection == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Section value is invalid"));
			} else {
				TrendingEventEntity entity = new TrendingEventEntity();
				entity.setAction(trendingAction == null ? null : trendingAction);
				entity.setContent(trendingRequestData.getContent());
				entity.setContentType(trendingRequestData.getContentType());
				entity.setImage(trendingRequestData.getImage());
				entity.setLanguage(trendingRequestData.getLanguage());
				entity.setOrder(trendingRequestData.getOrder());
				entity.setSection(homeSection);
				entity.setShortContent(trendingRequestData.getShortContent());
				entity.setThumbnail(trendingRequestData.getThumbnail());
				entity.setTitle(trendingRequestData.getTitle());
				entity.setUrl(trendingRequestData.getUrl());
				
				entity = trendingEventService.saveTrending(entity);
				
				apiResponse.setData(entity);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PutMapping(value = "/admin/v1/trending_event/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> updateTrendingEvent(@PathVariable(name = "id") final int id, 
													@RequestBody final TrendingEventDTO trendingRequestData, final Account account) {
		
		ApiRespObject<TrendingEventEntity> apiResponse = new ApiRespObject<TrendingEventEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			TrendingEventEntity entity = trendingEventService.getById(id);
			if(entity == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Trending id does not existed"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			TrendingEventActionEntity trendingAction = trendingEventService.getTrendingActionById(trendingRequestData.getAction());
			HomeSectionEntity homeSection = trendingEventService.getHomeSectionById(trendingRequestData.getSection());
			
			if(homeSection == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Section value is invalid"));
			} else {
				entity.setAction(trendingAction == null ? null : trendingAction);
				entity.setContent(trendingRequestData.getContent());
				entity.setContentType(trendingRequestData.getContentType());
				entity.setImage(trendingRequestData.getImage());
				entity.setLanguage(trendingRequestData.getLanguage());
				entity.setOrder(trendingRequestData.getOrder());
				entity.setSection(homeSection);
				entity.setShortContent(trendingRequestData.getShortContent());
				entity.setThumbnail(trendingRequestData.getThumbnail());
				entity.setTitle(trendingRequestData.getTitle());
				entity.setUrl(trendingRequestData.getUrl());
				entity.setActivated(trendingRequestData.getActivated());
				
				entity = trendingEventService.saveTrending(entity);
				
				apiResponse.setData(entity);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}

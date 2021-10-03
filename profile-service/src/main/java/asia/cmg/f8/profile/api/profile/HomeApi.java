package asia.cmg.f8.profile.api.profile;

import asia.cmg.f8.profile.domain.client.CmsClient;
import asia.cmg.f8.profile.domain.client.CommerceClient;
import asia.cmg.f8.profile.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespListObject;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.config.LeepWebConfiguration;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.client.FollowUserClient;
import asia.cmg.f8.profile.domain.entity.ActivityEntity;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventEntity;
import asia.cmg.f8.profile.domain.service.CounterService;
import asia.cmg.f8.profile.domain.service.ActivityService;
import asia.cmg.f8.profile.domain.service.TrendingActionService;
import asia.cmg.f8.profile.domain.service.TrendingNewsEventService;
import asia.cmg.f8.profile.partner.leep_web.LeepEventService;
import asia.cmg.f8.profile.partner.leep_web.LeepContentEntity;
import asia.cmg.f8.profile.partner.leep_web.LeepNewsService;
import asia.cmg.f8.profile.partner.leep_web.LeepPostService;
import asia.cmg.f8.profile.util.SocialConstant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class HomeApi {

	@Autowired
	private TrendingNewsEventService trendingService;

	@Autowired
	private LeepWebConfiguration leepWebCofig;

	@Autowired
	private UserProfileProperties profileProperties;

	@Autowired
	private TrendingActionService trendingActionService;

	@Autowired
	private ActivityService postService;

	@Autowired
	private CounterService counterService;

	@Autowired
	private FollowUserClient followUserClient;

	@Autowired
	private CommerceClient commerceClient;

	@Autowired
	private CmsClient cmsClient;

	private static final Logger LOG = LoggerFactory.getLogger(HomeApi.class);

	private final int INIT_SECTION_LIMIT = 10;
	private final String VN_LOCALE = "vi";
	private final String EN_LOCALE = "en";
	private final int SECTION_TRENDING = 100;
	private final int SECTION_BANNER = 200;
	private final int SECTION_NEWS = 300;

	private final int LATEST_NEWS_SECTION = 100;
	private final int TRENDING_VIDEOS_SECTION = 400;
	private final int FEATURED_VIDEO_SECTION = 500;
	private final int SUGGESTED_TRAINERS_SECTION = 600;
	private final int POPULAR_POST_SECTION = 700;
	private final int BANNER_EVENT_SECTION = 200;
	private final int NEWS_SECTION = 300;
	private final int NEAR_BY_CLUBS_SECTION = 800;

	private final int RANDOM_NUMOFVIEWS_MIN = 1000;
	private final int RANDOM_NUMOFVIEWS_MAX = 3000;

	@GetMapping(value = "/mobile/v1/home/trendingnewsevent", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTrendAndEvent(@RequestParam("section_id") final Integer sectionId,
												@RequestParam("section_code") final Integer sectionCode,
												@RequestParam("page") final Integer page,
												@RequestParam("limit") final Integer size,
												@RequestParam("language") final String language,
												Account account) {
		ApiRespObject<PageResponse<TrendingEventEntity>> apiResponse = new ApiRespObject<PageResponse<TrendingEventEntity>>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		if(!VN_LOCALE.equalsIgnoreCase(language) && !EN_LOCALE.equalsIgnoreCase(language)) {
			return new ResponseEntity<Object>(ErrorCode.REQUEST_INVALID.withDetail("Language is not valid"), HttpStatus.OK);
		}

		try {
			PageResponse<TrendingEventEntity> pagedResult = null;
			if(sectionCode == SECTION_NEWS) {
				pagedResult = this.getPagedLeepNews(null, page, size);
			} else {
				Pageable pageable = new PageRequest(page, size);
				pagedResult = trendingService.getBySectionId(sectionId, language, pageable);
			}

			apiResponse.setData(pagedResult);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v1/home", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> homeInitialize(@RequestParam("language") final String language, Account account) {
		ApiRespListObject<HomeSectionDTO<TrendingEventEntity>> apiResponse = new ApiRespListObject<HomeSectionDTO<TrendingEventEntity>>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			List<HomeSectionDTO<TrendingEventEntity>> homeInitData = new ArrayList<HomeSectionDTO<TrendingEventEntity>>();
			List<HomeSectionEntity> homeSections = trendingService.getAllSections();

			if(homeSections.isEmpty()) {
				return new ResponseEntity<Object>(ErrorCode.ENTRY_NOT_EXIST.withDetail("Have no any home sections in db"), HttpStatus.OK);
			}

			homeSections.forEach(section -> {
				HomeSectionDTO<TrendingEventEntity> homeItem = new HomeSectionDTO<TrendingEventEntity>();

				if(section.getSectionCode() == SECTION_TRENDING) {
					final int INDEX = 0;
					final int COUNT = 5;

					List<TrendingEventEntity> leepNews = this.getTopLeepContent(section, INDEX, COUNT);
					if(!leepNews.isEmpty()) {
						homeItem.sectionId = section.getId();
						if(VN_LOCALE.equalsIgnoreCase(language)) {
							homeItem.sectionName = section.getSectionNameVI();
						} else {
							homeItem.sectionName = section.getSectionNameEN();
						}
						homeItem.sectionType = section.getSectionType().toString();
						homeItem.order = section.getOrder();
						homeItem.items = leepNews;

						homeInitData.add(homeItem);
					}
				} else if(section.getSectionCode() == SECTION_BANNER) {		// Get Trending section data
					List<TrendingEventEntity> trendingEventList = trendingService.getBySectionId(section.getId(), language, INIT_SECTION_LIMIT);

					if(!trendingEventList.isEmpty()) {
						homeItem.sectionId = section.getId();
						homeItem.sectionCode = section.getSectionCode();
						if(VN_LOCALE.equalsIgnoreCase(language)) {
							homeItem.sectionName = section.getSectionNameVI();
						} else {
							homeItem.sectionName = section.getSectionNameEN();
						}
						homeItem.sectionType = section.getSectionType().toString();
						homeItem.order = section.getOrder();
						homeItem.items = trendingEventList;

						homeInitData.add(homeItem);
					}
				} else if(section.getSectionCode() == SECTION_NEWS) {	// Get News&Event section data from external leep DB
					final int INDEX = 5;
					final int COUNT = 5;

					List<TrendingEventEntity> leepNews = this.getTopLeepContent(section, INDEX, COUNT);
					List<TrendingEventEntity> leepEvent = this.getTopLeepEvent(section);
					List<TrendingEventEntity> leepNewsAndEvent = new ArrayList<TrendingEventEntity>();
					leepNewsAndEvent.addAll(leepNews);
					leepNewsAndEvent.addAll(leepEvent);

					if(!leepNewsAndEvent.isEmpty()) {
						homeItem.sectionId = section.getId();
						if(VN_LOCALE.equalsIgnoreCase(language)) {
							homeItem.sectionName = section.getSectionNameVI();
						} else {
							homeItem.sectionName = section.getSectionNameEN();
						}
						homeItem.sectionType = section.getSectionType().toString();
						homeItem.order = section.getOrder();
						homeItem.items = leepNewsAndEvent;

						homeInitData.add(homeItem);
					}
				}
			});

			apiResponse.setData(homeInitData);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v2/home", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> homeInitialize_V2(@RequestParam("language") final String language,
													@RequestParam("latitude") final Double latitude,
													@RequestParam("longitude") final Double longitude,
													Account account,
													@RequestHeader("Authorization") String authToken) {
		ApiRespListObject<HomeSectionDTO<?>> apiResponse = new ApiRespListObject<HomeSectionDTO<?>>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			List<HomeSectionDTO<?>> homeSections = new ArrayList<HomeSectionDTO<?>>();
			List<HomeSectionEntity> homeSectionsEntity = trendingService.getAllActiveSections();
			HomeSectionDTO<?> section = null;

			if(homeSectionsEntity.isEmpty()) {
				return new ResponseEntity<Object>(ErrorCode.ENTRY_NOT_EXIST.withDetail("Have no any home sections in db"), HttpStatus.OK);
			}

			for (HomeSectionEntity sectionEntity : homeSectionsEntity) {

				switch (sectionEntity.getSectionCode()) {
					case LATEST_NEWS_SECTION:
						if((section = this.bindingLatestNewsSection(sectionEntity, language)) != null) {
							homeSections.add(section);
						}
						break;
					case TRENDING_VIDEOS_SECTION:
						if((section = this.bindingTrendingVideosSection(sectionEntity, sectionEntity.getAccountUuid(), sectionEntity.getMaxItem(), language)) != null) {
							homeSections.add(section);
						}
						break;
					case FEATURED_VIDEO_SECTION:
						if((section = this.bindingFeaturedVideosSection(sectionEntity, sectionEntity.getMaxItem(), language)) != null) {
							homeSections.add(section);
						}
						break;
					case SUGGESTED_TRAINERS_SECTION:
						if((section = this.bindingSuggestedTrainers(sectionEntity, latitude, longitude, sectionEntity.getMaxItem(), language, account, authToken)) != null) {
							homeSections.add(section);
						}
						break;
					case POPULAR_POST_SECTION:
						if((section = this.bindingPopularPostsSection(sectionEntity, sectionEntity.getAccountUuid(), sectionEntity.getMaxItem(), language)) != null) {
							homeSections.add(section);
						}
						break;
					case BANNER_EVENT_SECTION:
						if((section = this.bindingBannerEventSection(sectionEntity, language)) != null) {
							homeSections.add(section);
						}
						break;
					case NEWS_SECTION:
						if((section = this.bindingNewsSection(sectionEntity, language)) != null) {
							homeSections.add(section);
						}
						break;
					case NEAR_BY_CLUBS_SECTION:
						if((section = this.bindingNearByClubs(sectionEntity, language, longitude, latitude, authToken)) != null) {
							homeSections.add(section);
						}
						break;
					default:
						break;
				}
			}

			apiResponse.setData(homeSections);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	private HomeSectionDTO<?> bindingNearByClubs(HomeSectionEntity sectionEntity, String language, double longitude, double latitude, String authToken) {
		try {
			Map<String, Object> data = cmsClient.nearbyClubs(longitude, latitude, 0, sectionEntity.getMaxItem(), authToken, language);
			return new HomeSectionDTO<>(sectionEntity, (List<?>)data.get("data"), language);
		} catch (Exception e) {
			LOG.error("bindingNearByClubs(HomeSectionEntity => ", e.getMessage());
			return null;
		}

	}

	private List<TrendingEventEntity> getTopLeepEvent(HomeSectionEntity homeSection) {
		List<TrendingEventEntity> trendingEvents = new ArrayList<TrendingEventEntity>();
		LeepEventService leepService = new LeepEventService(leepWebCofig);

		List<LeepContentEntity> leepEvents = leepService.getTopLeepEvents();
		leepEvents.forEach(event -> {
			TrendingEventActionEntity trendingAction = trendingActionService.getById(event.action_id);
			TrendingEventEntity trendingEventEntity = LeepEventService.convert(homeSection, event, trendingAction);
			trendingEvents.add(trendingEventEntity);
		});

		return trendingEvents;
	}

	private PageResponse<TrendingEventEntity> getPagedLeepEvents(final HomeSectionEntity homeSection, final int page, final int size) {
		PageResponse<TrendingEventEntity> pagedResult = new PageResponse<TrendingEventEntity>();
		List<TrendingEventEntity> trendingEvents = new ArrayList<TrendingEventEntity>();
		LeepEventService leepService = new LeepEventService(leepWebCofig);

		List<LeepContentEntity> leepEvents = leepService.getPagedLeepEvents(page, size);
		leepEvents.forEach(event -> {
			TrendingEventActionEntity trendingAction = trendingActionService.getById(event.action_id);
			TrendingEventEntity trendingEventEntity = LeepEventService.convert(homeSection, event, trendingAction);
			trendingEvents.add(trendingEventEntity);
		});

		pagedResult.setCount(leepEvents.size());
		pagedResult.setItems(trendingEvents);
		pagedResult.setPage(page);
		pagedResult.setSize(size);

		return pagedResult;
	}

	private List<TrendingEventEntity> getTopLeepPosts(HomeSectionEntity homeSection, final int index, final int limit) {
		List<TrendingEventEntity> trendingEvents = new ArrayList<TrendingEventEntity>();
		LeepPostService leepService = new LeepPostService(leepWebCofig, profileProperties);

		List<LeepContentEntity> leepNews = leepService.getTopLeepPosts(index, limit);
		leepNews.forEach(event -> {
			TrendingEventActionEntity trendingAction = trendingActionService.getById(event.action_id);
			TrendingEventEntity trendingEventEntity = LeepPostService.convert(homeSection, event, trendingAction);
			trendingEvents.add(trendingEventEntity);
		});

		return trendingEvents;
	}

	private List<TrendingEventEntity> getTopLeepContent(HomeSectionEntity homeSection, final int index, final int limit) {
		List<TrendingEventEntity> trendingEvents = new ArrayList<TrendingEventEntity>();
		LeepNewsService leepService = new LeepNewsService(leepWebCofig);

		List<LeepContentEntity> leepNews = leepService.getTopLeepContent(index, limit);
		leepNews.forEach(event -> {
			TrendingEventActionEntity trendingAction = trendingActionService.getById(event.action_id);
			TrendingEventEntity trendingEventEntity = LeepEventService.convert(homeSection, event, trendingAction);
			trendingEvents.add(trendingEventEntity);
		});

		return trendingEvents;
	}

	private PageResponse<TrendingEventEntity> getPagedLeepNews(final HomeSectionEntity homeSection, final int page, final int size) {
		PageResponse<TrendingEventEntity> pagedResult = new PageResponse<TrendingEventEntity>();
		List<TrendingEventEntity> trendingEvents = new ArrayList<TrendingEventEntity>();
		LeepNewsService leepService = new LeepNewsService(leepWebCofig);

		List<LeepContentEntity> leepEvents = leepService.getPagedLeepNews(page, size);
		leepEvents.forEach(event -> {
			TrendingEventActionEntity trendingAction = trendingActionService.getById(event.action_id);
			TrendingEventEntity trendingEventEntity = LeepEventService.convert(homeSection, event, trendingAction);
			trendingEvents.add(trendingEventEntity);
		});

		pagedResult.setCount(leepEvents.size());
		pagedResult.setItems(trendingEvents);
		pagedResult.setPage(page);
		pagedResult.setSize(size);

		return pagedResult;
	}

	private Post convertActivityToPost(final ActivityEntity entity, final int numberOfLikes,
										final int numberOfComments, final int numberOfViews, final List<String> taggedUuids) {
		final String postId = TextUtils.isEmpty(entity.getRequestUuid()) ? entity.getUuid() : entity.getRequestUuid();

		return Post.builder()
				.postId(postId)
				.content(entity.getText())
				.contentType(entity.getContentType())
				.published(entity.getPublished())
				.numberOfComments(numberOfComments)
				.numberOfLikes(numberOfLikes)
				.numberOfViews(numberOfViews)
				.thumbnailImageLink(entity.getThumbnailImageLink())
				.requestUuid(entity.getRequestUuid())
				.taggedUuids(taggedUuids)
				.links(entity.getLinks())
				.videoDuration(entity.getVideoDuration() == null ? 0 : entity.getVideoDuration()).build();
	}

	private int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	private HomeSectionDTO<TrendingEventEntity> bindingBannerEventSection(HomeSectionEntity sectionEntity, final String language) {
		try {
			List<TrendingEventEntity> trendingEventList = trendingService.getBySectionId(sectionEntity.getId(), language, sectionEntity.getMaxItem());
			if(!trendingEventList.isEmpty()) {
				return new HomeSectionDTO<TrendingEventEntity>(sectionEntity, trendingEventList, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingBannerEventSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<TrendingEventEntity> bindingLatestNewsSection(HomeSectionEntity sectionEntity, final String language) {
		try {
			final int INDEX = 0;
			final int COUNT = 5;
			List<TrendingEventEntity> leepNews = this.getTopLeepPosts(sectionEntity, INDEX, COUNT);
			if(!leepNews.isEmpty()) {
				return new HomeSectionDTO<TrendingEventEntity>(sectionEntity, leepNews, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingLatestNewsSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<TrendingEventEntity> bindingNewsSection(HomeSectionEntity sectionEntity, final String language) {
		try {
			final int INDEX = 5;
			final int COUNT = 10;

			List<TrendingEventEntity> leepPosts = this.getTopLeepPosts(sectionEntity, INDEX, COUNT);

			if(!leepPosts.isEmpty()) {
				return new HomeSectionDTO<TrendingEventEntity>(sectionEntity, leepPosts, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingNewsSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<Post> bindingPopularPostsSection(HomeSectionEntity sectionEntity, final String ownerUuid, final int limit, final String language) {
		try {
			List<ActivityEntity> activityEntities = postService.getActivitiesByOwner(ownerUuid, limit);
			List<Post> posts = this.populateDataActivity(activityEntities);
			if(!posts.isEmpty()) {
				return new HomeSectionDTO<Post>(sectionEntity, posts, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingPopularPostsSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<Post> bindingTrendingVideosSection(HomeSectionEntity sectionEntity, final String ownerUuid, final int limit, final String language) {
		try {
			List<ActivityEntity> activityEntities = postService.getVideoPostsByOwner(ownerUuid, limit);

			List<Post> posts = this.populateDataActivity(activityEntities);
			if(!posts.isEmpty()) {
				return new HomeSectionDTO<Post>(sectionEntity, posts, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingTrendingVideosSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<Post> bindingFeaturedVideosSection(HomeSectionEntity sectionEntity, final int limit, final String language) {
		try {
			List<ActivityEntity> activityEntities = postService.getSponsoredVideoPosts(limit);
			List<Post> posts = this.populateDataActivity(activityEntities);
			if(!posts.isEmpty()) {
				return new HomeSectionDTO<Post>(sectionEntity, posts, language);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("[bindingFeaturedVideosSection] error detail: {}", e.getMessage());
			return null;
		}
	}

	private HomeSectionDTO<SuggestedTrainersDTO> bindingSuggestedTrainers(HomeSectionEntity sectionEntity,
																		  final Double latitude,
																		  final Double longitude,
																		  int limit,
																		  final String language,
																		  final Account loggedInUser,
																		  String authToken) {
		try {
			SuggestedTrainerResponse suggestedTrainerResponse =
					commerceClient.suggestTrainers(latitude, longitude, 0, limit, authToken);
			suggestedTrainerResponse.getData().forEach(suggestedTrainer -> {
				if (!loggedInUser.uuid().equalsIgnoreCase(suggestedTrainer.getUuid())) {
					Boolean followStatus = followUserClient.checkFollowConnection(loggedInUser.uuid(),
							suggestedTrainer.getUuid()).getContent();
					suggestedTrainer.setFollowStatus(followStatus);
				}
			});
			return new HomeSectionDTO<>(sectionEntity, suggestedTrainerResponse.getData(), language);
		} catch (Exception e) {
			LOG.error("[bindingSuggestedTrainers] error detail: {}", e.getMessage());
			return null;
		}
	}

	private List<Post> populateDataActivity(final List<ActivityEntity> activityEntities) {

        final Set<String> listPostUuid = new HashSet<>();
        final Set<String> listPostRequestUuid = new HashSet<>();
        List<Post> result = null;

        try {
        	for (final ActivityEntity activity : activityEntities) {
                listPostUuid.add(activity.getUuid());
                final String postRequestUuid = activity.getRequestUuid();
                if (postRequestUuid != null) {
                    listPostRequestUuid.add(postRequestUuid);
                }
            }

        	Map<String, Integer> likeAndCommentCounter = counterService.getNumberOfLikesCommentOfPost(listPostUuid, listPostRequestUuid).toBlocking().firstOrDefault(new HashMap<String, Integer>());

        	result = activityEntities
        					.stream()
                            .map(entity -> {
                                final String postId = entity.getUuid();
                                final String requestUuid = entity.getRequestUuid();
                                String counterName = SocialConstant.getViewOfPostCounterName(postId);

                            	int numberOfViewsOfPost = likeAndCommentCounter.getOrDefault(counterName, 0);
                            	if(numberOfViewsOfPost == 0) {
                            		numberOfViewsOfPost = getRandomNumberInRange(RANDOM_NUMOFVIEWS_MIN, RANDOM_NUMOFVIEWS_MAX);
                            		counterService.randomNumberOfViewsOfPost(counterName, numberOfViewsOfPost);
                            	}

                                return convertActivityToPost(
		                                        entity,
		                                        likeAndCommentCounter.getOrDefault(SocialConstant.getLikeOfPostCounterName(postId), 0),
		                                        likeAndCommentCounter.getOrDefault(SocialConstant.getCommentOfPostCounterName(requestUuid), 0),
		                                        numberOfViewsOfPost,
		                                        entity.getTaggedUuids());
                            })
                            .collect(Collectors.toList());
        	return result;
		} catch (Exception e) {
			LOG.error("[populateDataActivity] error detail: {}", e.getMessage());
			return Collections.emptyList();
		}
    }
}

package asia.cmg.f8.profile.api.profile;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.Utilities;
import asia.cmg.f8.profile.api.question.QuestionApi;
import asia.cmg.f8.profile.api.question.UserResource;
import asia.cmg.f8.profile.api.question.UserResourceAssembler;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.database.entity.LevelEntity;
import asia.cmg.f8.profile.database.entity.QuestionEntity;
import asia.cmg.f8.profile.database.entity.UserPtiMatchEntity;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.Profile;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.repository.LevelEntityRepository;
import asia.cmg.f8.profile.domain.repository.UserPTiMatchRepository;
import asia.cmg.f8.profile.domain.service.ElasticSearchService;
import asia.cmg.f8.profile.domain.service.ProfileService;
import asia.cmg.f8.profile.domain.service.QuestionPTiMatchService;
import asia.cmg.f8.profile.domain.service.QuestionQuery;
import asia.cmg.f8.profile.domain.service.UserDistrictLocationService;
import asia.cmg.f8.profile.domain.service.UserProfileService;
import asia.cmg.f8.profile.dto.CustomProfileDTO;
import asia.cmg.f8.profile.dto.LocationDistanceDTO;
import asia.cmg.f8.profile.dto.QuestionPTiMatch;
import asia.cmg.f8.profile.dto.SuggestedTrainersDTO;
import asia.cmg.f8.profile.dto.UserPTiMatchDTO_V2;
import asia.cmg.f8.profile.dto.UserProfileResponse;
import asia.cmg.f8.profile.dto.UserProfileResponse_V2;

/**
 * Created on 2/22/17.
 */
@RestController
public class SearchTrainerApi {

	private final ElasticSearchService elasticSearchService;
	private final UserResourceAssembler userResourceAssembler;
	private final UserProfileService userProfileService;
	private final PagedResourcesAssembler<UserEntity> pagedResourcesAssembler;

	private final String AGE_QUESTION_KEY = "Age";
	private final String GENDER_QUESTION_KEY = "Gender";
	private final String DISTRICT_QUESTION_KEY = "District";

	private final Double MAX_RADIUS_IN_KM = 30d;
	private static final Logger LOG = LoggerFactory.getLogger(SearchTrainerApi.class);

	@Autowired
	private QuestionPTiMatchService questionsPTIService;

	@Autowired
	private UserPTiMatchRepository userPTiMatchRepo;

	@Autowired
	private UserDistrictLocationService districtLocationService;

	@Autowired
	private UserProfileProperties configProperties;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private LevelEntityRepository levelRepository;

	public SearchTrainerApi(final ElasticSearchService elasticSearchService,
			final UserResourceAssembler userResourceAssembler, final UserProfileService userProfileService,
			final PagedResourcesAssembler<UserEntity> pagedResourcesAssembler) {

		this.elasticSearchService = elasticSearchService;
		this.userResourceAssembler = userResourceAssembler;
		this.userProfileService = userProfileService;
		this.pagedResourcesAssembler = pagedResourcesAssembler;
	}

	/**
	 * Currently not used Checked by Thach Vo
	 * 
	 * @param timestamp
	 * @param pageable
	 * @param account
	 * @param languageContext
	 * @return
	 */
	@RequestMapping(value = "/matchedtrainers", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PagedResources<UserResource> matchedTrainers(@QueryParam(value = "timestamp") final Long timestamp,
			@PageableDefault(size = 8) final Pageable pageable, final Account account,
			final LanguageContext languageContext) {

		// Adding timestamp param to url.
		final Long time = (timestamp == null) ? Instant.now().toEpochMilli() : timestamp;
		final String timestampUri = ControllerLinkBuilder.linkTo(QuestionApi.class).toString() + "?timestamp=" + time;
		final Link link = new Link(timestampUri);

		final String language = languageContext.language();
		final Map<String, String> attributes = userProfileService.getAttributeListsAsMap(language, account);

		final Page<UserEntity> trainers = elasticSearchService.matchedTrainer(account, time, pageable).map(source -> {
			final Optional<Profile> profile = Optional.ofNullable(source.getProfile());
			if (profile.isPresent()) {
				final Profile finalProfile = Profile.builder().from(profile.get())
						.withLocalizedData(Collections.singletonMap(Locale.forLanguageTag(language), attributes))
						.build();
				return UserEntity.builder().from(source).withProfile(finalProfile).build();
			}
			return source;
		});
		return pagedResourcesAssembler.toResource(trainers, userResourceAssembler, link);
	}

	@RequestMapping(value = "/mobile/v1/trainers/matching", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> matchingTrainerBasedOnboarQuestions(
			@RequestParam(name = "cursor", required = false) String cursor,
			@RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		String SEARCH_APPROVED_TRAINERS = "select * where userType = 'pt' and activated = true and status.document_status = 'approved' order by profile.rated desc";

		try {
			if (StringUtils.isEmpty(cursor)) {
				cursor = null;
			}

			List<UserEntity> matchedTrainers = new ArrayList<UserEntity>();
			List<AnswerEntity> userAnswers = questionsPTIService.getAnswersByUserUuid(account.ugAccessToken(),
					account.uuid());
			Map<String, AnswerEntity> mapOfUSerAnswer = new HashMap<String, AnswerEntity>();
			userAnswers.forEach(answer -> {
				if (answer.getQuestionId().compareToIgnoreCase(AGE_QUESTION_KEY) == 0
						|| answer.getQuestionId().compareToIgnoreCase(GENDER_QUESTION_KEY) == 0
						|| answer.getQuestionId().compareToIgnoreCase(DISTRICT_QUESTION_KEY) == 0) {
					mapOfUSerAnswer.put(answer.getQuestionId(), answer);
				}
			});

			if (mapOfUSerAnswer.isEmpty()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("EU does not have any answers"));
				return new ResponseEntity<Object>(HttpStatus.OK);
			}

			PagedUserResponse<UserEntity> pagedTrainers = userProfileService
					.searchUsersByQuery(SEARCH_APPROVED_TRAINERS, cursor, limit);

			if (pagedTrainers != null) {
				List<UserEntity> trainers = pagedTrainers.getEntities();

				for (UserEntity trainer : trainers) {
					boolean matchedTrainer = true;
					List<AnswerEntity> trainerAnswers = questionsPTIService
							.getAnswersByUserUuid(account.ugAccessToken(), trainer.getUuid());
					trainerAnswers = trainerAnswers.stream().filter(
							trainerAnswer -> ((trainerAnswer.getQuestionId().compareToIgnoreCase(AGE_QUESTION_KEY) == 0
									|| trainerAnswer.getQuestionId().compareToIgnoreCase(GENDER_QUESTION_KEY) == 0
									|| trainerAnswer.getQuestionId().compareToIgnoreCase(DISTRICT_QUESTION_KEY) == 0)))
							.collect(Collectors.toList());

					for (AnswerEntity trainerAnswer : trainerAnswers) {
						AnswerEntity userAnswer = mapOfUSerAnswer.get(trainerAnswer.getQuestionId());
						if (userAnswer != null) {
							boolean isMatched = questionsPTIService.isMatchingOneOfAnswers(userAnswer, trainerAnswer);
							if (isMatched == false) {
								matchedTrainer = false;
								break;
							}
						}
					}

					if (matchedTrainer == true) {
						matchedTrainers.add(trainer);
					}
				}

				pagedTrainers.setEntities(matchedTrainers);
				pagedTrainers.setCount(matchedTrainers.size());
				PagedUserResponse<UserProfileResponse> pagedUserProfileResponse = this.populateCustomData(account,
						pagedTrainers);
				apiResponse.setData(pagedUserProfileResponse);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v1/trainers/search_all", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchPTs(@RequestParam(name = "cursor", required = false) String cursor,
			@RequestParam(name = "limit", required = false) Integer limit, final Account account) {
		String SEARCH_TRAINERS = "select * where userType = 'pt' and activated = true and status.document_status = 'approved' order by profile.rated desc";
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();

		try {
			if (Objects.isNull(limit)) {
				limit = 0;
			}
			if (StringUtils.isEmpty(cursor)) {
				cursor = null;
			}
			PagedUserResponse<UserEntity> pagedUsers = userProfileService.searchUsersByQuery(SEARCH_TRAINERS, cursor,
					limit);

			if (!Objects.isNull(pagedUsers)) {
				PagedUserResponse<UserProfileResponse> user = this.populateCustomData(account, pagedUsers);
				apiResponse.setData(user);
			}

			apiResponse.setStatus(ErrorCode.SUCCESS);

		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v1/trainers/search_by_skills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchPTsBySkill(@RequestParam(name = "cursor", required = false) String cursor,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "skills", required = true) final String skills, final Account account) {
		String SEARCH_TRAINERS_BY_SKILL = String.format(
				"select * where userType = 'pt' and activated = true and status.document_status = 'approved' and profile.skills contains '*%s*' order by profile.rated desc",
				skills);
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();

		try {
			if (Objects.isNull(limit)) {
				limit = 0;
			}
			if (StringUtils.isEmpty(cursor)) {
				cursor = null;
			}

			PagedUserResponse<UserEntity> pagedUsers = userProfileService.searchUsersByQuery(SEARCH_TRAINERS_BY_SKILL,
					cursor, limit);
			if (!Objects.isNull(pagedUsers)) {
				PagedUserResponse<UserProfileResponse> user = this.populateCustomData(account, pagedUsers);
				apiResponse.setData(user);
			}

			apiResponse.setStatus(ErrorCode.SUCCESS);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v1/trainers/search_is_subscribe", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchPTsEnabledSubs(@RequestParam(name = "cursor", required = false) String cursor,
			@RequestParam(name = "limit", required = false) Integer limit, final Account account) {
		String SEARCH_TRAINERS_ISSUBSCRIPTION = "select * where userType = 'pt' and activated = true and status.document_status = 'approved' and enable_subscribe = true order by profile.rated desc";
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();

		try {
			if (Objects.isNull(limit)) {
				limit = 0;
			}
			if (StringUtils.isEmpty(cursor)) {
				cursor = null;
			}

			PagedUserResponse<UserEntity> pagedUsers = userProfileService
					.searchUsersByQuery(SEARCH_TRAINERS_ISSUBSCRIPTION, cursor, limit);
			if (!Objects.isNull(pagedUsers)) {
				PagedUserResponse<UserProfileResponse> user = this.populateCustomData(account, pagedUsers);
				apiResponse.setData(user);
			}

			apiResponse.setStatus(ErrorCode.SUCCESS);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v2/trainers/search_by_skills", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchPTsBySkill_V2(@PageableDefault(size = 50, page = 0) final Pageable pageable,
			@RequestParam(name = "skills", required = true) final Set<String> skills, final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!account.isEu()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not EU user type"));
			} else {
				// Build LIMIT syntax
				int offset = 0;
				int size = pageable.getPageSize();
				if (pageable.getPageNumber() > 0) {
					offset = pageable.getPageSize() * pageable.getPageNumber();
				}

				List<UserPTiMatchDTO_V2> pagedResult = userPTiMatchRepo.getByEuUuidAndPtSkills_V2(account.uuid(),
						skills, offset, size);

				if (!pagedResult.isEmpty()) {
					PagedUserResponse<UserProfileResponse_V2> response = this.populateCustomData_V2(pagedResult);
					apiResponse.setData(response);
				} else {
					apiResponse.setStatus(ErrorCode.FAILED.withDetail("Have no matched trainers"));
				}
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v2/trainers/search_is_subscribe", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchPTsEnabledSubs_V2(@PageableDefault(size = 50, page = 0) final Pageable pageable,
			final Account account) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!account.isEu()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not EU user type"));
			} else {
				// Build LIMIT syntax
				int offset = 0;
				int size = pageable.getPageSize();
				if (pageable.getPageNumber() > 0) {
					offset = pageable.getPageSize() * pageable.getPageNumber();
				}

				List<UserPTiMatchDTO_V2> pagedResult = userPTiMatchRepo
						.getByEuUuidAndPtEnableSubscribe_V2(account.uuid(), offset, size);

				if (!pagedResult.isEmpty()) {
					PagedUserResponse<UserProfileResponse_V2> response = this.populateCustomData_V2(pagedResult);
					apiResponse.setData(response);
				} else {
					apiResponse.setStatus(ErrorCode.FAILED.withDetail("Have no matched trainers"));
				}
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v1/trainers/top_ptimatch", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getTopMatchedTrainers(@PageableDefault(size = 50, page = 0) final Pageable pageable,
			@RequestParam(name = "level", required = false) final String level, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!account.isEu()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not EU user type"));
			} else {
				Page<UserPtiMatchEntity> pagedResult = null;

				if (StringUtils.isEmpty(level)) {
					pagedResult = userPTiMatchRepo.getByEuUuid(account.uuid(), pageable);
				} else {
					List<String> levels = Utilities.getLowerOrEqualLevels(level);
					if (!levels.isEmpty()) {
						pagedResult = userPTiMatchRepo.getByEuUuidAndPtLevel(account.uuid(), levels, pageable);
					}
				}

				if (pagedResult != null && !pagedResult.getContent().isEmpty()) {
					List<UserPtiMatchEntity> ptiMatchedList = pagedResult.getContent();
					PagedUserResponse<UserProfileResponse> response = this.populateCustomData(ptiMatchedList);
					apiResponse.setData(response);
				} else {
					if (pageable.getPageNumber() == 0) {
						LOG.info("Finding sugggestion trainers for user {} ... ", account.uuid());
						List<String> recommendedTrainers = this.findRecommendedTrainers(account.uuid(),
								configProperties.getSuggestTrainersLimit());
						PagedUserResponse<UserProfileResponse> response = this.populateCustomData(account.uuid(),
								recommendedTrainers);
						apiResponse.setData(response);
					}
				}
			}

		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/mobile/v2/trainers/top_ptimatch", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getTopMatchedTrainers_V2(
			@PageableDefault(size = 50, page = 0) final Pageable pageable,
			@RequestParam(name = "level", required = false) final String level, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!account.isEu()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Not EU user type"));
			} else {
				List<UserPTiMatchDTO_V2> pagedResult = Collections.emptyList();

				// Build LIMIT syntax
				int offset = 0;
				int size = pageable.getPageSize();
				if (pageable.getPageNumber() > 0) {
					offset = pageable.getPageSize() * pageable.getPageNumber();
				}

				if (StringUtils.isEmpty(level)) {
					pagedResult = userPTiMatchRepo.getByEuUuid_V2(account.uuid(), offset, size);
				} else {
					List<String> levels = Utilities.getLowerOrEqualLevels(level);
					if (!levels.isEmpty()) {
						pagedResult = userPTiMatchRepo.getByEuUuidAndPtLevel_V2(account.uuid(), levels, offset, size);
					}
				}

				if (!pagedResult.isEmpty()) {
					PagedUserResponse<UserProfileResponse_V2> response = this.populateCustomData_V2(pagedResult);
					apiResponse.setData(response);
				} else {
					if (pageable.getPageNumber() == 0) {
						LOG.info("Finding sugggestion trainers for user {} ... ", account.uuid());
						List<String> recommendedTrainers = this.findRecommendedTrainers(account.uuid(),
								configProperties.getSuggestTrainersLimit());
						PagedUserResponse<UserProfileResponse_V2> response = this.populateCustomData_V2(account.uuid(),
								recommendedTrainers);
						apiResponse.setData(response);
					}
				}
			}

		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/internal/v1/profile/user/{uuid}/suggested-trainers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> findSuggestedTrainers(@PathVariable("uuid") String accountUuid,
			@RequestParam("latitude") Double latitude, @RequestParam("longitude") Double longitude,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		List<SuggestedTrainersDTO> result = new ArrayList<SuggestedTrainersDTO>();
		try {
			result = districtLocationService.findTopNearestTrainers(accountUuid, latitude, longitude,
					pageable.getPageSize());
		} catch (Exception e) {
			// TODO: handle exception
		}

		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

	private List<String> findRecommendedTrainers(final String userUuid, int limit) {
		List<String> result = new ArrayList<String>();
		try {
			List<String> nearestTrainers = findNearestTrainerBaseOnDistrictLocation(userUuid, limit);
			if (nearestTrainers.isEmpty()) {
				LOG.info("[findRecommendedTrainers] Not found any nearest trainers");
				List<String> leaderTrainers = profileService.findAmbassadorOrLeaderTrainers(limit);
				result.addAll(leaderTrainers);
			} else {
				LOG.info("[findRecommendedTrainers] Found {} nearest trainers: {}", nearestTrainers.size(),
						nearestTrainers);
				result.addAll(nearestTrainers);
				limit = configProperties.getSuggestTrainersLimit() - nearestTrainers.size();
				if (limit > 0) {
					List<String> leaderTrainers = profileService.findAmbassadorOrLeaderTrainers(nearestTrainers, limit);
					LOG.info("[findRecommendedTrainers] And found {} leader/ambassador trainers: {}",
							leaderTrainers.size(), leaderTrainers);
					result.addAll(leaderTrainers);
				}
			}

			return result;
		} catch (Exception e) {
			LOG.error("[findRecommendedTrainers] exception detail: ", e.getMessage());
			return Collections.emptyList();
		}
	}

	/**
	 * Finding nearest Trainers base on district location (latitude/longtitude)
	 * 
	 * @param userUuid
	 * @return List<String> of trainer's uuid
	 */
	private List<String> findNearestTrainerBaseOnDistrictLocation(final String userUuid, final int limit) {

		try {
			List<LocationDistanceDTO> nearestTrainers = districtLocationService.findTopNearestDistricts(userUuid,
					MAX_RADIUS_IN_KM, limit);

			List<String> trainerUuidList = nearestTrainers.stream().map(nearestTrainer -> nearestTrainer.getUserUuid())
					.collect(Collectors.toList());

			return trainerUuidList;
		} catch (Exception e) {
			LOG.error("[findNearestTrainerBaseOnDistrictLocation] exception detail: ", e.getMessage());
			return Collections.emptyList();
		}
	}

	private PagedUserResponse<UserProfileResponse> populateCustomData(List<UserPtiMatchEntity> ptiMatchedList) {

		List<UserProfileResponse> userProfileList = ptiMatchedList.stream().map(userPTiMatch -> {
			String ptUuid = userPTiMatch.getPtUuid();
			UserEntity userInfo = userProfileService.getUser(ptUuid);

			if (userInfo != null) {
				Profile profile = userInfo.getProfile();
				boolean isFollowing = userProfileService.checkFollowing(userPTiMatch.getEuUuid(),
						userPTiMatch.getPtUuid());
				UserType userType = userInfo.getUserType();
				ExtendUserType extendUserType = userInfo.getExtUserType();
				if (extendUserType == null && UserType.PT == userType) {
					extendUserType = ExtendUserType.PT_NORMAL;
				}
				CustomProfileDTO customProfile = null;
				if (profile != null) {
					customProfile = CustomProfileDTO.builder().setBirthday(profile.getBirthday())
							.setCovers(profile.covers()).setPhone(profile.getPhone()).setSkills(profile.getSkills())
							.setTagline(profile.getTagline()).setBio(profile.getBio()).build();
				}

				return UserProfileResponse.builder().setUuid(userInfo.getUuid()).setUsername(userInfo.getUsername())
						.setName(userInfo.getName()).setUserType(userType).setExtendUserType(extendUserType)
						.isFollowing(isFollowing).isActivated(userInfo.getActivated())
						.setApproved(userInfo.getApproved())
						.setRated(profile == null ? 0
								: (userInfo.getProfile().getRated() == null ? 0 : userInfo.getProfile().getRated()))
						.setPicture(Optional.ofNullable(userInfo.getPicture()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setLevel(Optional.ofNullable(userInfo.getLevel()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setProfile(customProfile).setStatus(userInfo.getStatus())
						.setEnableSubscribe(
								userInfo.getEnableSubscribe() == null ? false : userInfo.getEnableSubscribe())
						.setUserPTiMatch(userPTiMatch).build();
			} else {
				return null;
			}
		}).filter(object -> object != null).collect(Collectors.toList());

		PagedUserResponse<UserProfileResponse> userProfileResponse = new PagedUserResponse<UserProfileResponse>();
		userProfileResponse.setCount(userProfileList.size());
		userProfileResponse.setEntities(userProfileList);

		return userProfileResponse;
	}

	private PagedUserResponse<UserProfileResponse_V2> populateCustomData_V2(List<UserPTiMatchDTO_V2> ptiMatchedList) {

		List<UserProfileResponse_V2> userProfileList = ptiMatchedList.stream().map(userPTiMatch -> {
			String ptUuid = userPTiMatch.getPtUuid();
			UserEntity userInfo = userProfileService.getUser(ptUuid);

			if (userInfo != null) {
				Profile profile = userInfo.getProfile();
				boolean isFollowing = userProfileService.checkFollowing(userPTiMatch.getEuUuid(),
						userPTiMatch.getPtUuid());
				UserType userType = userInfo.getUserType();
				ExtendUserType extendUserType = userInfo.getExtUserType();
				if (extendUserType == null && UserType.PT == userType) {
					extendUserType = ExtendUserType.PT_NORMAL;
				}
				CustomProfileDTO customProfile = null;
				if (profile != null) {
					customProfile = CustomProfileDTO.builder().setBirthday(profile.getBirthday())
							.setCovers(profile.covers()).setPhone(profile.getPhone()).setSkills(profile.getSkills())
							.setTagline(profile.getTagline()).setBio(profile.getBio()).build();
				}

				return UserProfileResponse_V2.builder().setUuid(userInfo.getUuid()).setUsername(userInfo.getUsername())
						.setName(userInfo.getName()).setUserType(userType).setExtendUserType(extendUserType)
						.isFollowing(isFollowing).isActivated(userInfo.getActivated())
						.setApproved(userInfo.getApproved())
						.setRated(profile == null ? 0
								: (userInfo.getProfile().getRated() == null ? 0 : userInfo.getProfile().getRated()))
						.setPicture(Optional.ofNullable(userInfo.getPicture()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setLevel(Optional.ofNullable(userInfo.getLevel()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setProfile(customProfile).setStatus(userInfo.getStatus())
						.setEnableSubscribe(
								userInfo.getEnableSubscribe() == null ? false : userInfo.getEnableSubscribe())
						.setUserPTiMatch(userPTiMatch).setPtBookingCredit(userPTiMatch.getPtBookingCredit()).build();
			} else {
				return null;
			}
		}).filter(object -> object != null).collect(Collectors.toList());
		userProfileList.sort((UserProfileResponse_V2 u1, UserProfileResponse_V2 u2) -> u2.getUserPTiMatch().getAverage()
				.compareTo(u1.getUserPTiMatch().getAverage()));
		PagedUserResponse<UserProfileResponse_V2> userProfileResponse = new PagedUserResponse<UserProfileResponse_V2>();
		userProfileResponse.setCount(userProfileList.size());
		userProfileResponse.setEntities(userProfileList);

		return userProfileResponse;
	}

	private PagedUserResponse<UserProfileResponse> populateCustomData(String userUuid, List<String> trainerUuidList) {

		List<UserProfileResponse> userProfileList = trainerUuidList.stream().map(trainerUuid -> {
			UserEntity userInfo = userProfileService.getUser(trainerUuid);

			if (userInfo != null) {
				LevelEntity ptLevelEntity = levelRepository.findByCode(userInfo.getLevel()).orElse(null);
				Profile profile = userInfo.getProfile();
				boolean isFollowing = userProfileService.checkFollowing(userUuid, trainerUuid);
				UserType userType = userInfo.getUserType();
				ExtendUserType extendUserType = userInfo.getExtUserType();
				if (extendUserType == null && UserType.PT == userType) {
					extendUserType = ExtendUserType.PT_NORMAL;
				}
				CustomProfileDTO customProfile = null;
				if (profile != null) {
					customProfile = CustomProfileDTO.builder().setBirthday(profile.getBirthday())
							.setCovers(profile.covers()).setPhone(profile.getPhone()).setSkills(profile.getSkills())
							.setTagline(profile.getTagline()).setBio(profile.getBio()).build();
				}

				return UserProfileResponse.builder().setUuid(userInfo.getUuid()).setUsername(userInfo.getUsername())
						.setName(userInfo.getName()).setUserType(userType).setExtendUserType(extendUserType)
						.isFollowing(isFollowing).isActivated(userInfo.getActivated())
						.setApproved(userInfo.getApproved())
						.setRated(profile == null ? 0
								: (userInfo.getProfile().getRated() == null ? 0 : userInfo.getProfile().getRated()))
						.setPicture(Optional.ofNullable(userInfo.getPicture()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setLevel(Optional.ofNullable(userInfo.getLevel()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setProfile(customProfile).setStatus(userInfo.getStatus())
						.setEnableSubscribe(
								userInfo.getEnableSubscribe() == null ? false : userInfo.getEnableSubscribe())
						.setUserPTiMatch(null).build();
			} else {
				return null;
			}
		}).filter(object -> object != null).collect(Collectors.toList());

		PagedUserResponse<UserProfileResponse> userProfileResponse = new PagedUserResponse<UserProfileResponse>();
		userProfileResponse.setCount(userProfileList.size());
		userProfileResponse.setEntities(userProfileList);

		return userProfileResponse;
	}

	private PagedUserResponse<UserProfileResponse_V2> populateCustomData_V2(String userUuid,
			List<String> trainerUuidList) {

		List<UserProfileResponse_V2> userProfileList = trainerUuidList.stream().map(trainerUuid -> {
			UserEntity userInfo = userProfileService.getUser(trainerUuid);

			if (userInfo != null) {
				LevelEntity ptLevelEntity = levelRepository.findByCode(userInfo.getLevel()).orElse(null);
				Profile profile = userInfo.getProfile();
				boolean isFollowing = userProfileService.checkFollowing(userUuid, trainerUuid);
				UserType userType = userInfo.getUserType();
				ExtendUserType extendUserType = userInfo.getExtUserType();
				if (extendUserType == null && UserType.PT == userType) {
					extendUserType = ExtendUserType.PT_NORMAL;
				}
				CustomProfileDTO customProfile = null;
				if (profile != null) {
					customProfile = CustomProfileDTO.builder().setBirthday(profile.getBirthday())
							.setCovers(profile.covers()).setPhone(profile.getPhone()).setSkills(profile.getSkills())
							.setTagline(profile.getTagline()).setBio(profile.getBio()).build();
				}

				return UserProfileResponse_V2.builder().setUuid(userInfo.getUuid()).setUsername(userInfo.getUsername())
						.setName(userInfo.getName()).setUserType(userType).setExtendUserType(extendUserType)
						.isFollowing(isFollowing).isActivated(userInfo.getActivated())
						.setApproved(userInfo.getApproved())
						.setRated(profile == null ? 0
								: (userInfo.getProfile().getRated() == null ? 0 : userInfo.getProfile().getRated()))
						.setPicture(Optional.ofNullable(userInfo.getPicture()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setLevel(Optional.ofNullable(userInfo.getLevel()).filter(StringUtils::isNotEmpty)
								.orElse(StringUtils.EMPTY))
						.setPtBookingCredit(ptLevelEntity == null ? 0 : ptLevelEntity.getPtBookingCredit())
						.setProfile(customProfile).setStatus(userInfo.getStatus())
						.setEnableSubscribe(
								userInfo.getEnableSubscribe() == null ? false : userInfo.getEnableSubscribe())
						.setUserPTiMatch(null).build();
			} else {
				return null;
			}
		}).filter(object -> object != null).collect(Collectors.toList());
		userProfileList.sort((UserProfileResponse_V2 u1, UserProfileResponse_V2 u2) -> u2.getUserPTiMatch().getAverage()
				.compareTo(u1.getUserPTiMatch().getAverage()));
		PagedUserResponse<UserProfileResponse_V2> userProfileResponse = new PagedUserResponse<UserProfileResponse_V2>();
		userProfileResponse.setCount(userProfileList.size());
		userProfileResponse.setEntities(userProfileList);

		return userProfileResponse;
	}

	private PagedUserResponse<UserProfileResponse> populateCustomData(Account account,
			PagedUserResponse<UserEntity> profiles) {

		Map<String, QuestionEntity> questionsMap = new HashMap<String, QuestionEntity>();
		List<asia.cmg.f8.profile.database.entity.QuestionEntity> questions = questionsPTIService
				.getQuestions(new QuestionQuery(UserType.PT.toString(), "en"));
		if (questions.isEmpty()) {
			throw new ConstraintViolationException("Question list is not initialized.", Collections.emptySet());
		} else {
			questions.forEach(question -> {
				questionsMap.put(question.key, question);
			});
		}

		List<UserProfileResponse> userList = profiles.getEntities().stream().map(userInfo -> {
			Profile profile = userInfo.getProfile();
			boolean isFollowing = userProfileService.checkFollowing(account.uuid(), userInfo.getUuid());
			UserType userType = userInfo.getUserType();
			ExtendUserType extendUserType = userInfo.getExtUserType();
			if (extendUserType == null && UserType.PT == userType) {
				extendUserType = ExtendUserType.PT_NORMAL;
			}

			List<QuestionPTiMatch> questionsPTIMatch = questionsPTIService.getQuestionsPTIMatch(account.ugAccessToken(),
					userInfo.getUuid(), questionsMap);

			CustomProfileDTO customProfile = null;
			if (profile != null) {
				customProfile = CustomProfileDTO.builder().setBirthday(profile.getBirthday())
						.setCovers(profile.covers()).setPhone(profile.getPhone()).setSkills(profile.getSkills())
						.setTagline(profile.getTagline()).setBio(profile.getBio()).build();
			}

			return UserProfileResponse.builder().setUuid(userInfo.getUuid()).setUsername(userInfo.getUsername())
					.setName(userInfo.getName()).setUserType(userType).setExtendUserType(extendUserType)
					.isFollowing(isFollowing).isActivated(userInfo.getActivated()).setApproved(userInfo.getApproved())
					.setRated(profile == null ? 0
							: (userInfo.getProfile().getRated() == null ? 0 : userInfo.getProfile().getRated()))
					.setPicture(Optional.ofNullable(userInfo.getPicture()).filter(StringUtils::isNotEmpty)
							.orElse(StringUtils.EMPTY))
					.setLevel(Optional.ofNullable(userInfo.getLevel()).filter(StringUtils::isNotEmpty)
							.orElse(StringUtils.EMPTY))
					.setQuestionsPTIMatch(questionsPTIMatch).setProfile(customProfile).setStatus(userInfo.getStatus())
					.setEnableSubscribe(userInfo.getEnableSubscribe() == null ? false : userInfo.getEnableSubscribe())
					.build();
		}).collect(Collectors.toList());

		PagedUserResponse<UserProfileResponse> userProfileResponse = new PagedUserResponse<UserProfileResponse>();
		userProfileResponse.setCount(profiles.getCount());
		userProfileResponse.setCursor(profiles.getCursor());
		userProfileResponse.setEntities(userList);

		return userProfileResponse;
	}
}

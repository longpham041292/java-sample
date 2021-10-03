package asia.cmg.f8.profile.api.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.GenderType;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.database.entity.BasicUserEntity;
import asia.cmg.f8.profile.database.entity.CityEntity;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.database.entity.QuestionOptionEntity;
import asia.cmg.f8.profile.database.entity.UserDistrictLocationEntity;
import asia.cmg.f8.profile.domain.client.AnswerClient;
import asia.cmg.f8.profile.domain.client.AttributeClient;
import asia.cmg.f8.profile.domain.client.CityClient;
import asia.cmg.f8.profile.domain.client.DistrictClient;
import asia.cmg.f8.profile.domain.client.QuestionClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.AnswerEntity;
import asia.cmg.f8.profile.domain.entity.Attribute;
import asia.cmg.f8.profile.domain.entity.CityCollection;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;
import asia.cmg.f8.profile.domain.entity.LocalizationEntity;
import asia.cmg.f8.profile.domain.entity.Option;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.Profile;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.repository.BasicUserEntityRepository;
import asia.cmg.f8.profile.domain.repository.CityRepository;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;
import asia.cmg.f8.profile.domain.repository.LocalizationRepository;
import asia.cmg.f8.profile.domain.repository.QuestionOptionRepository;
import asia.cmg.f8.profile.domain.repository.QuestionRepository;
import asia.cmg.f8.profile.domain.service.UserDistrictLocationService;
import feign.Body;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@RestController
public class DataMigrationApi {

	@Autowired
	QuestionClient questionClient;
	
	@Autowired
	AnswerClient answerClient;
	
	@Autowired
	QuestionRepository questionRepo;
	
	@Autowired
	QuestionOptionRepository questionOptionRepo;
	
	@Autowired
	AttributeClient attributeClient;
	
	@Autowired
	LocalizationRepository localizationRepo;
	
	@Autowired
	CityClient cityClient;
	
	@Autowired
	DistrictClient districtClient;
	
	@Autowired
	CityRepository cityRepo;
	
	@Autowired
	DistrictRepository districtRepo;
	
	@Autowired
	UserDistrictLocationService userLocationService;
	
	@Autowired
	UserClient userClient;
	
	@Autowired
	BasicUserEntityRepository basicUserRepo;
	
	private static final Logger LOG = LoggerFactory.getLogger(DataMigrationApi.class);
	
	@RequiredAdminRole
	@RequestMapping(value = "/admin/v1/migration/user_district_location", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> migrateUserDistrictLocation(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String cursor = null;
		
		try {
			do {
				PagedUserResponse<UserEntity> pagedResponse = userClient.searchUsersWithCursor("where status.document_status  = 'approved'", 1000, cursor);
				if(pagedResponse != null) {
					cursor = pagedResponse.getCursor();
					List<UserEntity> users = pagedResponse.getEntities();
					for (UserEntity user : users) {
						String ugSQL = "where owner = '%s' AND questionId = 'District'";
						ugSQL = String.format(ugSQL, user.getUuid());
						UserGridResponse<AnswerEntity> answersResponse = answerClient.getAnswersBySystem(ugSQL, 20);
						if(answersResponse != null) {
							List<AnswerEntity> answers = answersResponse.getEntities();
							
							for (AnswerEntity answerEntity : answers) {
								
								Set<String> districtKeys = answerEntity.getOptionKeys();
								List<UserDistrictLocationEntity> userDistrictLocations = new ArrayList<UserDistrictLocationEntity>();
								
								for (String districtKey : districtKeys) {
									List<DistrictEntity> districts = districtRepo.findByKeyAndLanguage(districtKey, "en");
									if(!districts.isEmpty()) {
										DistrictEntity district = districts.get(0);
										
										UserDistrictLocationEntity userLocation = new UserDistrictLocationEntity();
										userLocation.setCityKey(district.getCityKey());
										userLocation.setDistrictKey(district.getKey());
										userLocation.setLatitude(district.getLatitude());
										userLocation.setLongtitude(district.getLongtitude());
										userLocation.setUserType(user.getUserType().name());
										userLocation.setUserUuid(user.getUuid());
										
										userDistrictLocations.add(userLocation);
									}
								}
								
								userLocationService.save(userDistrictLocations, user.getUuid());
							}
						}
					}
				}
			}while(cursor != null);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/user_phone", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> migratePhoneOfUserProfile(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String cursor = null;
		
		try {
			if(!account.isAdmin()) {
				apiResponse.setStatus(ErrorCode.FORBIDDEN.withDetail("Need admin role for calling"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			do {
				PagedUserResponse<UserEntity> pagedResponse = userClient.searchUsersWithCursor("SELECT *", 1000, cursor);
				if(pagedResponse != null) {
					cursor = pagedResponse.getCursor();
					List<UserEntity> users = pagedResponse.getEntities();
					for (UserEntity user : users) {
						if(user.getProfile().getPhone() != null && !user.getProfile().getPhone().isEmpty()) {
							BasicUserEntity basicUser = basicUserRepo.findByUuid(user.getUuid());
							if(basicUser != null && basicUser.getPhone() == null) {
								basicUser.setPhone(user.getProfile().getPhone());
								basicUserRepo.save(basicUser);
								LOG.info("Already migrated user {} phone {}", basicUser.getUuid(), basicUser.getPhone());
							}
						}
					}
				}
			}while(cursor != null);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/validated-email", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> migrateValidatedEmail(@RequestBody Map<String, String> requestBody, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String cursor = null;
		
		try {
			String grantType = requestBody.get("grant_type");
			if(!StringUtils.isEmpty(grantType))
			{
				do {
					PagedUserResponse<UserEntity> pagedResponse = userClient.searchUsersWithCursor(String.format("where grant_type = '%s'", grantType), 1000, cursor);
					if(pagedResponse != null) {
						cursor = pagedResponse.getCursor();
						List<UserEntity> users = pagedResponse.getEntities();
						for (UserEntity user : users) {
							UserEntity copiedUserEntity = UserEntity.copyOf(user);
							copiedUserEntity = copiedUserEntity.withEmailvalidated(Boolean.TRUE);
							userClient.updateProfile(copiedUserEntity, copiedUserEntity.getUuid());
							LOG.info("Already updated validated email is true of user {} - {}", copiedUserEntity.getUuid(), copiedUserEntity.getUsername());
						}
					}
				} while(cursor != null);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/cities", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> migrateCitiesAndDistricts(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String ugGetAllQuery = "SELECT *";
		String ugGetDistrictsByCityKeyQuery = "SELECT * WHERE city_key = '%s' and language = '%s'";
		
		try {
			if(!account.isAdmin()) {
				apiResponse.setStatus(ErrorCode.FORBIDDEN.withDetail("Need admin role for calling"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
				
			UserGridResponse<CityCollection> ugCities = cityClient.getCitiesByQuery(ugGetAllQuery, 100);
			if(!ugCities.isEmpty()) {
				List<CityCollection> cities = ugCities.getEntities();
				for (CityCollection city : cities) {
					UserGridResponse<DistrictCollection> ugDistricts = districtClient.getDistrictsByQuery( String.format(ugGetDistrictsByCityKeyQuery, city.getKey(), city.getLanguage()), 100);
					if(!ugDistricts.isEmpty()) {
						CityEntity dbCity = this.convertData(city, ugDistricts.getEntities());
						cityRepo.save(dbCity);
					}
				}
			}
			
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/lists", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> migrateListsCollection(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			if(!account.isAdmin()) {
				apiResponse.setStatus(ErrorCode.FORBIDDEN.withDetail("Need admin role for calling"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			UserGridResponse<Attribute> ugResponse = attributeClient.getAttributes(1000);
			if(ugResponse != null) {
				List<Attribute> attributes = ugResponse.getEntities();
				attributes.forEach(attribute -> {
					LocalizationEntity localization = convert(attribute);
					localizationRepo.saveAndFlush(localization);
				});
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/questions", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	public ResponseEntity<Object> migrateQuestions(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			
			if(!account.isAdmin()) {
				apiResponse.setStatus(ErrorCode.FORBIDDEN.withDetail("Need admin role for calling"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			UserGridResponse<QuestionEntity> response = questionClient.getQuestions("select *", 1000);
			List<QuestionEntity> ugQuestions = response.getEntities();
			
			ugQuestions.forEach(ugQuestion -> {
				asia.cmg.f8.profile.database.entity.QuestionEntity dbQuestion = this.convertData(ugQuestion);
				if(dbQuestion != null) {
					questionRepo.saveAndFlush(dbQuestion);
				} else {
					LOG.error("Convert data failed with ug question uuid: {}", ugQuestion.getUuid());
				}
			});
			
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOG.error("[migrateQuestions][error: {}]", e.getMessage());
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequiredAdminRole
	@RequestMapping(value = "/admin/v1/migration/users", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> migrateUserInfo(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		int limit = 1000;
		String cursor = null;
		String SEARCH_ACTIVE_PT_QUERY = "select * where created >= 1590944400000";
		try {
			do {
				PagedUserResponse<UserEntity> pagedResponse = userClient.searchUsersWithCursor(SEARCH_ACTIVE_PT_QUERY, limit, cursor);
				if(pagedResponse != null) {
					cursor = pagedResponse.getCursor();
					List<UserEntity> users = pagedResponse.getEntities();
					for (UserEntity user : users) {
						BasicUserEntity basicUser = basicUserRepo.findByUuid(user.getUuid());
						if(basicUser == null) {
							Profile profile = user.getProfile();
							basicUser = new BasicUserEntity();
							LocalDateTime createdDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(user.getCreated()), TimeZone.getDefault().toZoneId());
							
							basicUser.setAvatar(user.getPicture());
							basicUser.setActivated(Boolean.TRUE);
							basicUser.setCity(profile == null ? null : profile.getCity());
							basicUser.setCountry(profile == null ? null : profile.getCountry());
							basicUser.setCreatedDate(createdDate);
							basicUser.setDocStatus(DocumentStatusType.ONBOARD);
							basicUser.setEmail(user.getEmail());
							basicUser.setEnableSubscribe(user.getEnableSubscribe() == null ? Boolean.FALSE : user.getEnableSubscribe());
							basicUser.setExtendUserType(user.getExtUserType() == null ? null : user.getExtUserType().name());
							basicUser.setFullName(user.getName());
							basicUser.setGender(profile == null ? GenderType.MALE.ordinal() : profile.getGender().ordinal());
							basicUser.setJoinDate(createdDate);
							basicUser.setPhone(profile == null ? null : profile.getPhone());
							basicUser.setUserName(user.getUsername());
							basicUser.setUserType(user.getUserType().name());
							basicUser.setUuid(user.getUuid());
							
							basicUserRepo.save(basicUser);
							LOG.info("Already migrated user {}", basicUser.getUuid());
						}
					}
				}
			} while(cursor != null);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/v1/migration/answers", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
	public ResponseEntity<Object> migrateAnswers(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String ugGetAnswersOfLocationQuestion = "select * where questionId = 'District'";
		String cursor = null;
		int limit = 1000;
		
		try {
			if (!account.isAdmin()) {
				apiResponse.setStatus(ErrorCode.FORBIDDEN.withDetail("Need admin role for calling"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			List<DistrictEntity> dbDistricts = districtRepo.findByLanguage("en");
			Map<String, DistrictEntity> mapDistricts = new HashMap<String, DistrictEntity>();
			dbDistricts.forEach(district -> {
				mapDistricts.put(district.getKey(), district);
			});
			
			do {
				PagedUserGridResponse<AnswerEntity> ugResponse = answerClient.getAnswersByUser( ugGetAnswersOfLocationQuestion, limit, cursor);
				
				if(!ugResponse.isEmpty()) {
					List<AnswerEntity> ugAnswers = ugResponse.getEntities();
					cursor = ugResponse.getCursor();
							
					for (AnswerEntity answerEntity : ugAnswers) {
						Set<String> districtKeys = answerEntity.getOptionKeys();
						for (String districtKey : districtKeys) {
							DistrictEntity dbDistrict = mapDistricts.get(districtKey);
							if(dbDistrict != null) {
								UserDistrictLocationEntity userLocation = new UserDistrictLocationEntity();
								userLocation.setCityKey(dbDistrict.getCityKey());
								userLocation.setDistrictKey(dbDistrict.getKey());
								userLocation.setLatitude(dbDistrict.getLatitude());
								userLocation.setLongtitude(dbDistrict.getLongtitude());
								userLocation.setUserType(answerEntity.getUserType().name());
								userLocation.setUserUuid(answerEntity.getOwner());
								
								userLocationService.save(userLocation);
							}
						}
					}
				}
			} while(cursor != null);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOG.error("[migrateQuestions][error: {}]", e.getMessage());
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	private CityEntity convertData(final CityCollection cityCollection, final List<DistrictCollection> districtsCollection) {
		CityEntity cityEntity = new CityEntity();
		
		cityEntity.setKey(cityCollection.getKey());
		cityEntity.setLanguage(cityCollection.getLanguage());
		cityEntity.setLatitude(cityCollection.getLatitude());
		cityEntity.setLongtitude(cityCollection.getLongtitute());
		cityEntity.setName(cityCollection.getName());
		cityEntity.setSequence(cityCollection.getSequence());
		cityEntity.setDistricts(this.convertDistrictData(districtsCollection));
		
		return cityEntity;
	}
	
	private List<DistrictEntity> convertDistrictData(final List<DistrictCollection> ugDistricts) {
		List<DistrictEntity> dbDistricts = new ArrayList<DistrictEntity>();
		
		for (DistrictCollection ugDistrict : ugDistricts) {
			DistrictEntity dbDistrict = new DistrictEntity();
			dbDistrict.setCityKey(ugDistrict.getCity_key());
			dbDistrict.setKey(ugDistrict.getKey());
			dbDistrict.setLanguage(ugDistrict.getLanguage());
			dbDistrict.setName(ugDistrict.getName());
			dbDistrict.setSequence(Integer.valueOf(ugDistrict.getSequence()));
			dbDistrict.setLatitude(ugDistrict.getLatitude());
			dbDistrict.setLongtitude(ugDistrict.getLongtitude());
			
			dbDistricts.add(dbDistrict);
		}
		
		return dbDistricts;
	}
	
	private asia.cmg.f8.profile.database.entity.QuestionEntity convertData(final QuestionEntity ugQuestion) {
		try {

			asia.cmg.f8.profile.database.entity.QuestionEntity dbQuestion = new asia.cmg.f8.profile.database.entity.QuestionEntity();
			dbQuestion.description = ugQuestion.getDescription();
			dbQuestion.filtered = ugQuestion.isFiltered();
			dbQuestion.hide = ugQuestion.isHide();
			dbQuestion.key = ugQuestion.getKey();
			dbQuestion.limitedUserSelection = ugQuestion.getLimitedUserSelection();

			List<QuestionOptionEntity> dbQuestionOptions = this.convertData(ugQuestion.getOptions());
			dbQuestion.setOptions(dbQuestionOptions);

			dbQuestion.questionType = ugQuestion.getQuestionType();
			dbQuestion.required = ugQuestion.isRequired();
			dbQuestion.sequence = Integer.valueOf(ugQuestion.getSequence());
			dbQuestion.tags = ugQuestion.getTags();
			dbQuestion.title = ugQuestion.getTitle();
			dbQuestion.usedFor = UserType.valueOf(ugQuestion.getUsedFor().toUpperCase());
			dbQuestion.weight = ugQuestion.getWeight();
			dbQuestion.language = ugQuestion.getLanguage();

			return dbQuestion;
		} catch (Exception e) {
			return null;
		}
	}
	
	private LocalizationEntity convert(Attribute attribute) {
		LocalizationEntity localization = new LocalizationEntity();
		
		localization.setCategory(attribute.getCategory());
		localization.setKey(attribute.getKey());
		localization.setLanguage(attribute.getLanguage());
		localization.setValue(attribute.getValue());
		
		return localization;
	}
	
	private List<QuestionOptionEntity> convertData(final List<Option> ugQuestionOptions) {
		try {
			List<QuestionOptionEntity> dbQuestionOptions = new ArrayList<QuestionOptionEntity>();
			
			ugQuestionOptions.forEach(ugQuestionOption -> {
				QuestionOptionEntity dbOption = new QuestionOptionEntity();
				dbOption.filtered = ugQuestionOption.isFiltered();
				dbOption.iconUrl = ugQuestionOption.getIconUrl();
				dbOption.key = ugQuestionOption.getKey();
				dbOption.sequence = ugQuestionOption.getOrder();
				dbOption.text = ugQuestionOption.getText();
				dbOption.type = ugQuestionOption.getType();
				dbOption.weight = ugQuestionOption.getWeight();
				
				dbQuestionOptions.add(dbOption);
			});
			
			return dbQuestionOptions;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}

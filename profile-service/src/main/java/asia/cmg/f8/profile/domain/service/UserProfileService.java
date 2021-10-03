package asia.cmg.f8.profile.domain.service;

import static java.util.stream.Collectors.toSet;

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

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.profile.ChangeUserInfoEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.Media;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.DateUtils;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.Utilities;
import asia.cmg.f8.profile.config.UserProfileProperties;
import asia.cmg.f8.profile.domain.client.AttributeClient;
import asia.cmg.f8.profile.domain.client.FollowUserClient;
import asia.cmg.f8.profile.domain.client.ProfileClient;
import asia.cmg.f8.profile.domain.client.SessionClient;
import asia.cmg.f8.profile.domain.client.UserClient;
import asia.cmg.f8.profile.domain.entity.Attribute;
import asia.cmg.f8.profile.domain.entity.ClubEntity;
import asia.cmg.f8.profile.domain.entity.LocalizationEntity;
import asia.cmg.f8.profile.domain.entity.PagedUserResponse;
import asia.cmg.f8.profile.domain.entity.Profile;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.domain.event.EventHandler;
import asia.cmg.f8.profile.domain.repository.LocalizationRepository;
import rx.Observable;

/**
 * Created on 11/10/16.
 */
@Service
public class UserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileService.class);
    private static final String ATTRIBUTE_BY_LANGUAGE_QUERY = "language='%s' AND not category = 'whos_hot' order by ordinal asc";
    private static final String ATTRIBUTE_BY_CATEGORY_QUERY = "language='%1$s' and category='%2$s'";
    private static final String CURRENT_USER = "me";
    private static final String FOLLOWING = "following";
    private static final String CONTRACTING = "contracting";
    private static final String VERIFICATION_ERROR = "One of the keys sent to server is not in %s list.";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String UUID = "uuid";
    public static final String REQUEST_DATA_IS_INVALID = "REQUEST_DATA_IS_INVALID";
    private static final int ATTRIBUTE_LIMIT = 1000;
    private static final int EMAIL_EXISTED = 9002;

    private enum MediaType {COVER, PHOTO, CLIENT, VIDEO}

    private enum AttributeType {INTEREST, EXPERIENCE, LANGUAGE, CREDENTIAL}

    private final UserClient userClient;
	private final UserProfileProperties properties;
    private final EventHandler eventHandler;
    private final AttributeClient attributeClient;
    private final ProfileClient profileClient;
    private final SessionClient sessionClient;
    private final FollowUserClient followUserClient;
    
    @Autowired
    LocalizationRepository localizationRepo;
   

    @Inject
    public UserProfileService(
            final UserClient userClient,
            final AttributeClient attributeClient,
            final EventHandler eventHandler,
            final UserProfileProperties properties,
            final ProfileClient profileClient,
            final SessionClient sessionClient,
            final FollowUserClient followUserClient) {
        this.userClient = userClient;
        this.properties = properties;
        this.eventHandler = eventHandler;
        this.attributeClient = attributeClient;
        this.profileClient = profileClient;
        this.sessionClient = sessionClient;
        this.followUserClient = followUserClient;
    }

    public Map<String, String> getAttributeListsAsMap(final String language, final Account account) {
        return getAttributeLists(language, account).stream().collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));
    }
    public Map<String, String> getAttributeListsAsMapPublic(final String language) {
        return getAttributeListsPublic(language).stream().collect(Collectors.toMap(Attribute::getKey, Attribute::getValue));
    }
    public List<Attribute> getAttributeLists(final String language, final Account account) {
        if (StringUtils.isEmpty(language)) {
            throw new IllegalArgumentException("Language variable is required for this action.");
        }

        final UserGridResponse<Attribute> response = attributeClient.getAttributes(
                String.format(ATTRIBUTE_BY_LANGUAGE_QUERY, language),
                ATTRIBUTE_LIMIT);

        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }
        return response.getEntities();
    }
    public List<Attribute> getAttributeListsPublic(final String language) {
        if (StringUtils.isEmpty(language)) {
            throw new IllegalArgumentException("Language variable is required for this action.");
        }

        final UserGridResponse<Attribute> response = attributeClient.getAttributes(
                String.format(ATTRIBUTE_BY_LANGUAGE_QUERY, language),
                ATTRIBUTE_LIMIT);

        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }
        return response.getEntities();
    }
    public List<Attribute> getAttributeLists(final Account account) {

        final UserGridResponse<Attribute> response = attributeClient.getAttributes(
                ATTRIBUTE_LIMIT);

        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }
        return response.getEntities();
    }

    public boolean updateTagline(final String tagline, final Account account) {
        return updateTagline(tagline, account.uuid());
    }
    
    public List<LocalizationEntity> getLocalizationByCategoryAndLanguage(List<String> categories, String language) {
    	List<LocalizationEntity> dataList = Collections.emptyList();
    	
    	try {
			dataList = localizationRepo.getByCategoriesAndLanguage(categories, language);
		} catch (Exception e) {
			return Collections.emptyList();
		}
    	
    	return dataList;
    }
    
    public List<LocalizationEntity> getLocalizationByLanguage(String language) {
    	List<LocalizationEntity> dataList = Collections.emptyList();
    	
    	try {
			dataList = localizationRepo.getByLanguage(language);
		} catch (Exception e) {
			return Collections.emptyList();
		}
    	
    	return dataList;
    }
    
    public boolean updateTagline(final String tagline, final String uuid) {
        final UserEntity userEntity = getUser(uuid);

        Profile profile = userEntity.getProfile();
        profile = Profile.builder().from(profile).withTagline(tagline).build();

        final UserEntity immutableUserEntity = UserEntity.copyOf(userEntity).withProfile(profile);

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }
    
    public boolean updateExtendUserType(final ExtendUserType extendUserType, final String uuid) {
        final UserEntity userEntity = getUser(uuid);

        final UserEntity immutableUserEntity = UserEntity.copyOf(userEntity).withExtUserType(extendUserType);

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }
    
	public boolean updateBio(final String bio, final Account account) {
		return updateBio(bio, account.uuid());
	}

	public boolean updateBio(final String bio, final String uuid) {
		final UserEntity userEntity = getUser(uuid);

		Profile profile = userEntity.getProfile();
		profile = Profile.builder().from(profile).withBio(bio).build();

		final UserEntity immutableUserEntity = UserEntity.copyOf(userEntity).withProfile(profile);

		return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
	}

    public boolean updateSkills(final List<String> skills, final Account account) {
        return updateSkills(skills,account.uuid());
    }
    
    public boolean updateSkills(final List<String> skills, final String uuid) {
        if (verifyAttribute(skills, AttributeType.INTEREST)) {
            throw new IllegalArgumentException(String.format(VERIFICATION_ERROR,
                    AttributeType.INTEREST.toString()));
        }
        final UserEntity userEntity = getUser(uuid);
        final Set<String> skillSet = skills.stream().distinct().collect(toSet());

        Profile profile = userEntity.getProfile();
        profile = Profile.builder().from(profile).withSkills(skillSet).build();

        final UserEntity immutableUserEntity = UserEntity.copyOf(userEntity).withProfile(profile);

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }

    public boolean updateCredentials(final List<String> credentials, final Account account) {
        return updateCredentials(credentials, account.uuid());
    }
    
    public boolean updateCredentials(final List<String> credentials, final String uuid) {
        if (verifyAttribute(credentials, AttributeType.CREDENTIAL)) {
            throw new IllegalArgumentException(String.format(VERIFICATION_ERROR,
                    AttributeType.CREDENTIAL.toString()));
        }

        UserEntity trainer = getUser(uuid);
        if (Objects.isNull(trainer)) {
            throw new IllegalArgumentException("User does not exist.");
        }

        if (Objects.isNull(trainer.getProfile())) {
            trainer = UserEntity.builder().from(trainer).withProfile(Profile.builder().build()).build();
        }

        final Set<String> credentialSet = credentials.stream().distinct().collect(toSet());

        Profile profile = trainer.getProfile();
        profile = Profile.builder().from(profile).withCredentials(credentialSet).build();

        final UserEntity immutableTrainer = UserEntity.builder().from(trainer)
                .withUuid(null)
                .withEmail(null)
                .withUsername(null)
                .withProfile(profile).build();

        final UserGridResponse<UserEntity> response = userClient.updateProfile(immutableTrainer, uuid);

        final boolean result = !response.isEmpty();
		if (result) {
			LOG.info(String.format("Updated User %s to UserGrid", uuid));
			fireUserInfoEvent(uuid, trainer.getUserType().toString(), trainer.getUsercode(), trainer.getClubcode(),trainer.getActivated());
		}

        return result;
    }

    public boolean updateLanguages(final List<String> languages, final Account account) {
        return updateLanguages(languages, account.uuid());
    }
    
    public boolean updateLanguages(final List<String> languages, final String uuid) {
        if (verifyAttribute(languages, AttributeType.LANGUAGE)) {
            throw new IllegalArgumentException(String.format(VERIFICATION_ERROR,
                    AttributeType.LANGUAGE.toString()));
        }

        final Set<String> languageSet = languages.stream().distinct().collect(toSet());

        final UserEntity userEntity = getUser(uuid);

        Profile profile = userEntity.getProfile();
        profile = Profile.builder().from(profile).withLanguages(languageSet).build();

        final UserEntity immutableUserEntity = UserEntity.builder()
                .from(userEntity)
                .withProfile(profile)
                .build();

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }
    
    public boolean updateClubOwnerInfo(final List<String> clubAmenities, final String clubOpeningHours, final String uuid) {

        final Set<String> clubAmenitySet = clubAmenities.stream().distinct().collect(toSet());
        final UserEntity userEntity = getUser(uuid);

        Profile profile = userEntity.getProfile();
        profile = Profile.builder().from(profile).withClubAmenities(clubAmenitySet).withClubOpeningHours(clubOpeningHours).build();

        final UserEntity immutableUserEntity = UserEntity.builder()
                .from(userEntity)
                .withProfile(profile)
                .build();

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }

    public boolean updateExperience(final String experience, final Account account) {
        return updateExperience(experience, account.uuid());
    }
    
    public boolean updateExperience(final String experience, final String uuid) {
        if (verifyAttribute(Collections.singletonList(experience), AttributeType.EXPERIENCE)) {
            throw new IllegalArgumentException(String.format(VERIFICATION_ERROR,
                    AttributeType.EXPERIENCE.toString()));
        }

        final UserEntity userEntity = getUser(uuid);

        Profile profile = userEntity.getProfile();
        profile = Profile.builder().from(profile).withExperiences(experience).build();

        final UserEntity immutableUserEntity = UserEntity.builder()
                .from(userEntity)
                .withProfile(profile)
                .build();

        return updateUser(immutableUserEntity, uuid, userEntity.getUserType().toString());
    }
    
    public boolean updateLastQuestionSequence(final String userUuid, final int lastSequence) {
    	final UserEntity userEntity = getUser(userUuid);

        final UserEntity immutableUserEntity = UserEntity.builder().from(userEntity).withLastQuestionSequence(lastSequence).build();

        return updateUser(immutableUserEntity, userUuid, userEntity.getUserType().toString());
    }

    public UserEntity getFullProfile(final String userId, final Account account, final String language, final asia.cmg.f8.profile.domain.entity.ProfileVersion version, final Boolean fullProfile) {
        String uuid = userId;
        if (StringUtils.equalsIgnoreCase(uuid, CURRENT_USER)) {
            uuid = account.uuid();
        }

        final UserGridResponse<UserEntity> response = userClient.getUser(uuid);
        if (response.isEmpty()) {
            throw new IllegalArgumentException("User does not exist in the system.");
        }

        final String currentLanguage = StringUtils.isEmpty(language) ? DEFAULT_LANGUAGE : language;

        UserEntity userEntity = response.getEntities().stream().findFirst().get();
        if(fullProfile == null || fullProfile == true) {
	        final Profile profile = userEntity.getProfile();
	
	        if (profile != null) {
	            final Profile composedProfile = Observable.zip(populateAttributes(profile, currentLanguage, account, version), counterFollowers(uuid),
	                    (Profile userProfile, Integer count) -> Profile.builder()
	                            .from(userProfile)
	                            .withFollowers(Optional.ofNullable(profile.getFollowers()).map(value -> value + count).orElse(count))
	                            .build())
	                    .toBlocking()
	                    .firstOrDefault(profile);
	
	            userEntity = UserEntity.builder().from(userEntity).withProfile(composedProfile).build();
	        }
        }
        
        
        return UserEntity.builder()
                .withName(userEntity.getName())
                .withEmail(userEntity.getEmail())
                .withUsername(userEntity.getUsername())
                .withActivated(userEntity.getActivated())
                .withUuid(userEntity.getUuid())
                .withPicture(userEntity.getPicture())
                .withLanguage(userEntity.getLanguage())
                .withUserType(userEntity.getUserType())
                .withCountry(userEntity.getCountry())
                .withProfile(userEntity.getProfile())
                .withStatus(userEntity.getStatus())
                .withLevel(userEntity.getLevel())
                .withApprovedDate(userEntity.getApprovedDate())
                .withDisplayApprovedDate(DateUtils.formatDateTime(userEntity.getApprovedDate(), properties.getDateTimeFormat()))
                .withUserRole(userEntity.getUserRole())
                .withUsercode(userEntity.getUsercode())
                .withClubcode(userEntity.getClubcode())
                .withEmailvalidated(userEntity.getEmailvalidated())
                .withPhoneValidated(userEntity.getPhoneValidated() == null ? Boolean.FALSE : userEntity.getPhoneValidated())
                .withEnableSubscribe(userEntity.getEnableSubscribe())
                .withExtUserType(this.getExtendUserType(userEntity.getUserType(), userEntity.getExtUserType()))
                .withEnablePrivateChat(userEntity.getEnablePrivateChat() == null ? Boolean.FALSE : userEntity.getEnablePrivateChat())
                .withOnBoardCompleted(userEntity.getOnBoardCompleted() == null ? Boolean.FALSE : userEntity.getOnBoardCompleted())
                .withUpdatedProfile(userEntity.getUpdatedProfile() == null ? true : userEntity.getUpdatedProfile())
                .build();
    }
    
    public UserEntity getFullProfilePublic(final String userUuid, final String language) {
    	
        final UserGridResponse<UserEntity> response = userClient.getUser(userUuid);
        if (response.isEmpty()) {
            throw new IllegalArgumentException("User does not exist in the system.");
        }

        UserEntity userEntity = response.getEntities().stream().findFirst().get();
        Profile profile = userEntity.getProfile().withPhone(null);
        
        if (profile != null) {
            final Profile composedProfile = Observable.zip(populateAttributesPublic(userUuid, profile, language), counterFollowers(userUuid),
                    (Profile userProfile, Integer count) -> Profile.builder()
                            .from(userProfile)
                            .withFollowers(Optional.ofNullable(profile.getFollowers()).map(value -> value + count).orElse(count))
                            .build())
                    .toBlocking()
                    .firstOrDefault(profile);

            userEntity = UserEntity.builder().from(userEntity).withProfile(composedProfile).build();
        }
        
        return UserEntity.builder()
                .withName(userEntity.getName())
                .withUsername(userEntity.getUsername())
                .withActivated(userEntity.getActivated())
                .withUuid(userEntity.getUuid())
                .withPicture(userEntity.getPicture())
                .withLanguage(userEntity.getLanguage())
                .withUserType(userEntity.getUserType())
                .withCountry(userEntity.getCountry())
                .withProfile(userEntity.getProfile())
                .withStatus(userEntity.getStatus())
                .withLevel(userEntity.getLevel())
                .withApprovedDate(userEntity.getApprovedDate())
                .withDisplayApprovedDate(DateUtils.formatDateTime(userEntity.getApprovedDate(), properties.getDateTimeFormat()))
                .withUserRole(userEntity.getUserRole())
                .withUsercode(userEntity.getUsercode())
                .withClubcode(userEntity.getClubcode())
                .withEmailvalidated(userEntity.getEmailvalidated())
                .withPhoneValidated(userEntity.getPhoneValidated() == null ? Boolean.FALSE : userEntity.getPhoneValidated())
                .withEnableSubscribe(userEntity.getEnableSubscribe())
                .withExtUserType(this.getExtendUserType(userEntity.getUserType(), userEntity.getExtUserType()))
                .withEnablePrivateChat(userEntity.getEnablePrivateChat() == null ? Boolean.FALSE : userEntity.getEnablePrivateChat())
                .withOnBoardCompleted(userEntity.getOnBoardCompleted() == null ? Boolean.FALSE : userEntity.getOnBoardCompleted())
                .build();
    }
    private Observable<Profile> populateAttributes(final Profile profile, final String language, final Account account, final asia.cmg.f8.profile.domain.entity.ProfileVersion version) {

        if(version ==null) {
            return Observable.fromCallable(() -> {

                final Map<String, String> attributes = getAttributeListsAsMap(language, account);
                return Profile.builder()
                        .from(profile)
                        .withLocalizedData(Collections.singletonMap(Locale.forLanguageTag(language), attributes))
                        .build();
            }).doOnError(throwable -> LOG.error("Error on populate attributes of user " + account.uuid(), throwable));
        }
        return  Observable.fromCallable(() -> {

            final List<Attribute> attributes = getAttributeLists(language, account);
            return Profile.builder()
                    .from(profile)
                    .withLocalized(attributes)
                    .build();
        }).doOnError(throwable -> LOG.error("Error on populate attributes of user " + account.uuid(), throwable));
    }
    
    private Observable<Profile> populateAttributesPublic(final String userUuid, final Profile profile, final String language) {

        return Observable.fromCallable(() -> {

            final Map<String, String> attributes = getAttributeListsAsMapPublic(language);
            return Profile.builder()
                    .from(profile)
                    .withLocalizedData(Collections.singletonMap(Locale.forLanguageTag(language), attributes))
                    .build();
        }).doOnError(throwable -> LOG.error("Error on populate attributes of user PT " + userUuid, throwable));
    }
    
    private Observable<Integer> counterFollowers(final String userId) {
        return Observable.fromCallable(() -> {
            return followUserClient.countFollowerConnection(userId).getContent().intValue();
        }).doOnError(throwable -> LOG.error("Error on count number of followers of user " + userId, throwable));
    }

    public List<Map<String, Object>> checkAssociation(final Map<String, Object> users, final Account account) {
        //Build query with OR.

        final List<String> listUUID = (List<String>) users.get(UUID);

       final List<String> followingUUID = new ArrayList<String>();
        listUUID.forEach(item -> {
        	if(checkFollowing(account.uuid(), item)) {
        		followingUUID.add(item);
        	}
        });
        final Set<String> contractingUUID =
                sessionClient.getContractingOfUser(account.uuid(), account.type(), listUUID);

        final List<Map<String, Object>> result = new ArrayList<>();

        //Build result
        listUUID.forEach(user -> {
            final Map<String, Object> item = new HashMap<>();
            item.put(UUID, user);
            item.put(FOLLOWING, followingUUID.contains(user));
            item.put(CONTRACTING, Objects.isNull(contractingUUID)
                    ? Boolean.FALSE : contractingUUID.contains(user));

            result.add(item);
        });

        return result;
    }
    
    public boolean checkFollowing(final String followingUuid, final String followerUuid) {
    	try {
    		return followUserClient.checkFollowConnection(followingUuid, followerUuid).getContent();
		} catch (Exception e) {
			return false;
		}
    }
    
    public boolean storeMediaInfo(final String type, final List<Media> medias, final Account account) {
        return storeMediaInfo(type, medias, account.uuid());
    }
    
    public boolean storeMediaInfo(final String type, final List<Media> medias, final String uuid) {
        if (!EnumUtils.isValidEnum(MediaType.class, type.toUpperCase(Locale.getDefault()))) {
            throw new IllegalArgumentException("Type of this media does not in the list.");
        }

        final MediaType mediaType = MediaType.valueOf(type.toUpperCase(Locale.getDefault()));

        if (medias.size() > getMaxConfigurationMediaItems(mediaType)) {
            throw new IllegalArgumentException(String.format("The number of %1$s is over limited %1$s of system.", type));
        }

        medias.forEach(media -> {
            if (StringUtils.isEmpty(media.getUrl())) {
                throw new IllegalArgumentException("You're sending wrong format or required URL for this media is missing.");
            }
            if (media.getCaption().length() > properties.getProfileMedia().getCaption()) {
                throw new IllegalArgumentException(String.format("The caption for %1$s is over the max length %2$s ",
                        type,
                        properties.getProfileMedia().getCaption()));
            }
        });

        final UserEntity userEntity = getUser(uuid);
        Profile profile = userEntity.getProfile();

        //Update profile
        switch (mediaType) {
            case COVER:
                profile = Profile.builder().from(profile).withCovers(medias).build();
                break;
            case PHOTO:
                profile = Profile.builder().from(profile).withImages(medias).build();
                break;
            case CLIENT:
                profile = Profile.builder().from(profile).withClients(medias).build();
                break;
            case VIDEO:
                profile = Profile.builder().from(profile).withVideos(medias).build();
                break;
            default:
                break;
        }

        final UserEntity updatedUser = UserEntity.builder()
                .from(userEntity)
                .withProfile(profile)
                .build();

        return updateUser(updatedUser, uuid, userEntity.getUserType().toString());

    }

    private int getMaxConfigurationMediaItems(final MediaType type) {
        final UserProfileProperties.ProfileMedia profileMedia = properties.getProfileMedia();
        switch (type) {
            case COVER:
                return profileMedia.getCover();
            case PHOTO:
                return profileMedia.getPhoto();
            case CLIENT:
                return profileMedia.getClient();
            case VIDEO:
                return profileMedia.getVideo();
            default:
                return 3;
        }
    }

    public UserEntity getCurrentUser(final Account account) {
        return getUser(account.uuid());
    }
    
    public UserEntity getUser(final String uuid) {
    	try {
    		final Optional<UserEntity> userEntity = userClient.getUser(uuid).getEntities().stream().findFirst();
    		
            if (!userEntity.isPresent()) {
            	return null;
            }

            if (Objects.isNull(userEntity.get().getProfile())) {
                return UserEntity.copyOf(userEntity.get()).withProfile(Profile.builder().build());
            }
            
            return userEntity.get();
		} catch (Exception e) {
			return null;
		}
    }
    
    private boolean updateUser(final UserEntity userEntity, final String uuid, final String userType) {
        final UserGridResponse<UserEntity> response = userClient.updateProfile(
                userEntity,
                uuid);

        final boolean result = !response.isEmpty();
        if (result) {
            LOG.info(String.format("Updated User %s to UserGrid", uuid));
            fireUserInfoEvent(uuid, userType, userEntity.getUsercode(), userEntity.getClubcode(), userEntity.getActivated());
        }

        return result;
    }

    private boolean verifyAttribute(
            final List<String> attributes,
            final AttributeType attributeType) {

        final List<Attribute> configuredAttributes = attributeClient.getAttributes(
                String.format(
                        ATTRIBUTE_BY_CATEGORY_QUERY,
                        DEFAULT_LANGUAGE,
                        attributeType.toString().toLowerCase(Locale.getDefault())),
                ATTRIBUTE_LIMIT).getEntities();

        if (configuredAttributes.isEmpty()) {
            throw new IllegalArgumentException("List of attribute is not configured in the system.");
        }
        final List<String> flatAttributes = configuredAttributes.stream().map(Attribute::getKey).map(String::toLowerCase).collect(Collectors.toList());
        return attributes.stream().map(String::toLowerCase).distinct().anyMatch(attribute -> !flatAttributes.contains(attribute));
    }
    
    public void fireUserInfoEvent(final String userId, final String userType, final String usercode, final String clubcode, final Boolean activated) {
        final ChangeUserInfoEvent changeUserInfoEvent = ChangeUserInfoEvent.newBuilder()
                .setEventId(java.util.UUID.randomUUID().toString())
                .setUserId(userId)
                .setUserType(userType)
                .setSubmittedAt(System.currentTimeMillis())
                .setActivate(activated.toString())
                .setUsercode(usercode)
                .setClubcode(clubcode)
                .build();

        LOG.info(String.format("Fire event for changing User Info: %s", userId));
        eventHandler.publish(changeUserInfoEvent);
    }

    public boolean updateProfile(final String uuid,
                                 final UserEntity profile,
                                 final String type, final boolean shouldSendEvent) {
        final UserGridResponse<UserEntity> res = profileClient.updateUserProfile(uuid, profile);
        if (Objects.isNull(res) || res.isEmpty()) {
            return Boolean.FALSE;
        }
        if(shouldSendEvent) {
            fireUserInfoEvent(uuid, type, profile.getUsercode(), profile.getClubcode(), profile.getActivated());  	
        }
   	
        return Boolean.TRUE;
    }

    public boolean updateProfilePicture(final String profileUrl, final String uuid) {
    	if (StringUtils.isEmpty(profileUrl)) {
            throw new IllegalArgumentException("You're sending wrong format or required URL for this profile picture is missing.");
        }


        final UserEntity userEntity = getUser(uuid);

        final UserEntity updatedUser = UserEntity.builder()
                .from(userEntity)
                .withPicture(profileUrl)
                .build();

        return updateUser(updatedUser, uuid, userEntity.getUserType().toString());

    }
    
	public boolean updateUserLevel(final String userLevel, final String uuid) {
		if (StringUtils.isEmpty(userLevel)) {
			throw new IllegalArgumentException("You're sending wrong format.");
		}

		final UserEntity userEntity = getUser(uuid);

		final UserEntity updatedUser = UserEntity.builder()
				.from(userEntity)
				.withLevel(userLevel)
				.build();

		return updateUser(updatedUser, uuid, userEntity.getUserType().toString());

	}
    
    public ImportUserResult checkUserCodeValid(final String newUserCode, final String userType) {
    	final ImportUserResult result = new ImportUserResult();
        if (StringUtils.isEmpty(newUserCode) ) {
        	result.setErrorCode(ErrorCode.INVALID_USER_CODE.getCode());
            return result;
        }
        
       final UserGridResponse<UserEntity> user = userClient.getUserByQuery("where usercode='" + newUserCode + "'");
        if (!user.isEmpty()) {
            LOG.warn("UserCode {} already exist", newUserCode);
            result.setErrorCode(ErrorCode.DUPLICATE_USER_CODE.getCode());
            return result;
        }   
        return sessionClient.isValidUserCodeInMigrationDB(newUserCode, userType);
    }
    
    public ErrorCode checkEmailValid(final String email) {
        final UserGridResponse<UserEntity> user = userClient.getUserByQuery("where email='" + email + "'");
        if (!user.isEmpty()) {
            LOG.info("Email {} already exist", email);
            return new ErrorCode(EMAIL_EXISTED,"","");
        }   
        return new ErrorCode(0, "", "");
    }
    
    private ExtendUserType getExtendUserType(final UserType userType, final ExtendUserType extUserType) {
    	if(UserType.PT == userType) {
    		if(extUserType == null) {
    			return ExtendUserType.PT_NORMAL;
    		}
    		return extUserType;
    	} else {
			return null;
		}
    }
    
    public PagedUserResponse<UserEntity> searchUsersByQuery(final String query, final String cursor, int pageSize) throws Exception {
		try {
			Utilities utils = new Utilities(properties);
			pageSize = utils.processUserSearchPagesize(pageSize);
			PagedUserResponse<UserEntity> pagedUsers = userClient.searchUsersWithCursor(query, pageSize, cursor);
			return Objects.isNull(pagedUsers) ? null : pagedUsers;
		} catch (Exception e) {
			LOG.error("[searchUsers][error: {}]", e.getMessage());
			throw e;
		}
	}
    
    public boolean addClubsOfOwner(final String userUuid, final List<ClubEntity> clubs) {
    	boolean result = this.deleteAllClubsOfOwner(userUuid);
    	
    	if(result == true) {
    		clubs.forEach(club -> {
    			PagedUserResponse<ClubEntity> response = userClient.addClubsOfOwner(userUuid, club.getId());
    			if(response == null) {
    				LOG.error("[addClubsOfOwner] Error on adding club [{}] to user [{}]", club.getId(), userUuid);
    			}
    		});
    		return true;
    	} else {
    		LOG.error("[addClubsOfOwner] Error on deleting all clubs of user [{}]", userUuid);
    		return false;
		}
    }
    
    public boolean deleteAllClubsOfOwner(final String userUuid) {
    	List<ClubEntity> clubs = this.getClubsFromOwner(userUuid);
    	
    	for (ClubEntity club : clubs) {
    		PagedUserResponse<ClubEntity> response = userClient.deleteClubOfOwner(userUuid, club.getId());
    		if(response == null) {
    			return false;
    		}
		}
    	
    	return true;
    }
    
    public List<ClubEntity> getClubsFromOwner(final String userUuid) {
    	try {
			PagedUserResponse<ClubEntity> response = userClient.getClubsFromOwner(userUuid);
			if(response == null || response.isEmpty()) {
				return Collections.emptyList();
			}
			return response.getEntities();
		} catch (Exception e) {
			LOG.error("[addClubsOfOwner] Error on getting clubs of user [{}]", userUuid);
			return Collections.emptyList();
		}
    }
    
    public List<UserEntity> getPTsbyFilter(final String userUuid) {
    	try {
			PagedUserResponse<UserEntity> response = userClient.getPTsInfo(userUuid);
			if(response == null || response.isEmpty()) {
				return Collections.emptyList();
			}
			return response.getEntities();
		} catch (Exception e) {
			LOG.error("[addClubsOfOwner] Error on getting clubs of user [{}]", userUuid);
			return Collections.emptyList();
		}
    }
}

package asia.cmg.f8.profile.domain.entity;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.user.DocumentStatusType;
import asia.cmg.f8.common.spec.user.IUserEntity;
import asia.cmg.f8.common.spec.user.UserType;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonSerialize(as = UserEntity.class)
@JsonDeserialize(builder = UserEntity.Builder.class)
@Document(indexName = "#{@getElasticIndice}", type = "#{@getElasticType}")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                Document.class,
                Id.class,
                JsonIgnoreProperties.class,
                JsonInclude.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@SuppressWarnings("CheckReturnValue")
public abstract class AbstractUserEntity implements IUserEntity {

    @SuppressWarnings("PMD")
    @JsonProperty("uuid")
    @Nullable
    @Id
    public abstract String getUuid();

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract String getEmail();

    @Nullable
    public abstract String getUsername();

    @Nullable
    public abstract Long getCreated();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public abstract String getSkills();

    @Nullable
    public abstract Boolean getActivated();

    @Nullable
    public abstract String getPicture();

    @Nullable
    public abstract String getLanguage();

    @Nullable
    public abstract UserType getUserType();

    @Nullable
    public abstract String getCountry();

    @Nullable
    public abstract Profile getProfile();

    @Nullable
    public abstract UserStatus getStatus();

    @Nullable
    public abstract Facebook getFacebook();

    @Nullable
    public abstract Long getApprovedDate();

    @Nullable
    public abstract String getDisplayApprovedDate();

    @Nullable
    public abstract String getUserRole();
    
    @Nullable
    public abstract String getUsercode();
    
    @Nullable
    public abstract String getClubcode();
    
    @Nullable
    public abstract Boolean getEmailvalidated();
    
    @JsonProperty("approved")
    @SuppressWarnings("PMD")
    public Boolean getApproved() {
        return getStatus() != null &&
                getStatus().documentStatus() != null &&
                getStatus().documentStatus() == DocumentStatusType.APPROVED;
    }
    
    @Nullable
    @JsonProperty("enable_subscribe")
    public abstract Boolean getEnableSubscribe();
    
    @Nullable
    @JsonProperty("last_question_sequence")
    public abstract Integer getLastQuestionSequence();
}

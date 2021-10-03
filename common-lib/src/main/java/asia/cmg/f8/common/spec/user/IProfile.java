package asia.cmg.f8.common.spec.user;

import asia.cmg.f8.common.spec.Localizable;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Created on 11/1/16.
 */
public interface IProfile extends Localizable {

    @JsonProperty("birthday")
    @Nullable
    String getBirthday();

    @JsonProperty("phone")
    @Nullable
    String getPhone();

    @JsonProperty("gender")
    @Nullable
    GenderType getGender();

    @JsonProperty("spoken_languages")
    @Nullable
    Set<String> getLanguages();

    @JsonProperty("skills")
    @Nullable
    Set<String> getSkills();

    @JsonProperty("tagline")
    @Nullable
    String getTagline();

    @JsonProperty("experience")
    @Nullable
    String getExperiences();

    @JsonProperty("country")
    @Nullable
    String getCountry();

    @JsonProperty("city")
    @Nullable
    String getCity();

    @JsonProperty("rated")
    @Nullable
    Double getRated();

    @JsonProperty("followers")
    @Nullable
    Integer getFollowers();

    @JsonProperty("images")
    @Nullable
    Set<Media> getImages();

    @JsonProperty("clients")
    @Nullable
    Set<Media> getClients();

    @JsonProperty("videos")
    @Nullable
    Set<Media> getVideos();

    @JsonProperty("credentials")
    @Nullable
    Set<String> getCredentials();

    @JsonProperty("covers")
    @Nullable
    Set<Media> covers();

    @JsonProperty("total_rate")
    @Nullable
    Double getTotalRate();

    @JsonProperty("number_of_rate")
    @Nullable
    Integer getNumberOfRate();
    
    @JsonProperty("bio")
    @Nullable
    String getBio();
    
    @JsonProperty("club_amenities")
    @Nullable
    List<String> getClubAmenities();
    
    @JsonProperty("club_opening_hours")
    @Nullable
    String getClubOpeningHours();
}

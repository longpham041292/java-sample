package asia.cmg.f8.user.domain.client;

import asia.cmg.f8.user.entity.Profile;
import asia.cmg.f8.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PTUserReportInfo {
    private String uuid;
    private String name;
    private String username;
    private String picture;
    private String country;
    private String city;
    private Boolean status;

    @JsonProperty("doc_status")
    private String docStatus;

    public void setDocStatus(final String docStatus) {
        this.docStatus = docStatus;
    }

    public PTUserReportInfo(final UserEntity user) {
        final Profile profile = user.getProfile();
        this.uuid = user.getUuid();
        this.name = user.getName();
        this.username = user.getUsername();
        this.picture = user.getPicture();
        this.country = profile.getCountry();
        this.city = profile.getCity();
        this.status = user.getActivated();
        this.docStatus = user.getStatus() == null ? null : String.valueOf(user
                .getStatus().documentStatus());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(final Boolean status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDocStatus() {
        return docStatus;
    }
}

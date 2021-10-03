package asia.cmg.f8.profile.api.question;


import asia.cmg.f8.profile.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by on 10/17/16.
 */
public class UserResource extends ResourceSupport {

    @JsonUnwrapped
    private UserEntity userEntity;

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(final UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}

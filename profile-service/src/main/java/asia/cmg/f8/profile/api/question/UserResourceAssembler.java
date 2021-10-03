package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

/**
 * Created by on 10/16/16.
 */
@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<UserEntity, UserResource> {

    public UserResourceAssembler() {
        super(QuestionApi.class, UserResource.class);
    }

    @Override
    public UserResource toResource(final UserEntity userEntity) {
        final UserResource userResource = createResourceWithId(userEntity.getUsername(), userEntity);

        userResource.setUserEntity(userEntity);

        return userResource;
    }

}

package asia.cmg.f8.user.entity;

/**
 * Created by on 11/9/16.
 */
public class ConversationResponse {
    private final String uuid;
    private final String name;
    private final String picture;

    public ConversationResponse(final String uuid, final String name, final String picture) {
        this.uuid = uuid;
        this.name = name;
        this.picture = picture;
    }

    public ConversationResponse(final UserEntity user) {
        this.uuid = user.getUuid();
        this.name = user.getName();
        this.picture = user.getPicture();
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }


}

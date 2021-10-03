package asia.cmg.f8.common.spec.social;

/**
 * Created on 11/14/16.
 */
public enum FollowingAction {

    FOLLOW("follow"), UNFOLLOW("unfollow");

    private final String fieldDescription;

    private FollowingAction(final String value) {
        fieldDescription = value;
    }

    @Override
    public String toString() {
        return fieldDescription;
    }
}

package asia.cmg.f8.profile.util;

/**
 * SocialConstant class represents constants of social service.
 * Created on 12/21/16.
 */
public final class SocialConstant {
    private SocialConstant() {

    }

    public static final int INCREASE = 1;
    public static final int DECREASE = -1;

    public static final String COMMENT_COUNTER_PATTERN = "%s_comment_counter";
    public static final String LIKE_COUNTER_PATTERN = "%s_like_counter";
    public static final String LIKE_COMMENT_COUNTER_PATTERN = "%s_like_comment_counter";
    public static final String VIEW_COUNTER_PATTERN = "%s_view_counter";

    public static final String POST_DETAIL_PAGE = "/posts/";

    /**
     * Get comment of post id Counter Name.
     *
     * @param postId post Id
     * @return Counter Name of Comment Post Counter
     */
    public static String getCommentOfPostCounterName(final String postId) {
        return String.format(COMMENT_COUNTER_PATTERN, postId);
    }

    /**
     * Get like of post id Counter Name.
     *
     * @param postId post Id
     * @return Counter Name of Like Post Counter
     */
    public static String getLikeOfPostCounterName(final String postId) {
        return String.format(LIKE_COUNTER_PATTERN, postId);
    }

    public static String getLikeCommentCounterName(final String commentId) {
        return String.format(LIKE_COMMENT_COUNTER_PATTERN, commentId);
    }
    
    public static String getViewOfPostCounterName(final String postId) {
    	return String.format(VIEW_COUNTER_PATTERN, postId);
    }
}

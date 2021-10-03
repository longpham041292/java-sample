package asia.cmg.f8.profile.domain.service;

/**
 * Created by tri.bui on 10/21/16.
 */
public class QuestionQuery {
    private final String type;
    private final String language;

    public QuestionQuery(final String type, final String language) {
        this.type = type;
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public String getLanguage() {
        return language;
    }
}

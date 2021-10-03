package asia.cmg.f8.profile.api.question;

import java.util.List;

/**
 * Created by tri.bui on 10/17/16.
 */
@SuppressWarnings("squid:S2384")
public class GetQuestionsResponse<E> {
    private final List<E> questions;

    public GetQuestionsResponse(final List<E> questions) {
        this.questions = questions;
    }

    public List<E> getQuestions() {
        return questions;
    }
}

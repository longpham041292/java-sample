package asia.cmg.f8.profile.domain.client;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModifyAnswerClientFallback implements ModifyAnswerClient {

    @Override
    public void deleteAnswersByUser(final String query, final int limit,
            final Map<String, Boolean> updateData) {
        // empty method
    }

}

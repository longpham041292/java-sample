package asia.cmg.f8.commerce.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class CommerceAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        // TODO Add logic to get auditor name
        return "admin";
    }

}

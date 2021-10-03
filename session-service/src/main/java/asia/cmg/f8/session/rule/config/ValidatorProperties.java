package asia.cmg.f8.session.rule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 12/15/16.
 */
@Component
@ConfigurationProperties(prefix = "session.validator")
public class ValidatorProperties {


    private double maxAllowedTimeInHours;

    public double getMaxAllowedTimeInHours() {
        return maxAllowedTimeInHours;
    }

    public void setMaxAllowedTimeInHours(final double maxAllowedTimeInHours) {
        this.maxAllowedTimeInHours = maxAllowedTimeInHours;
    }
}

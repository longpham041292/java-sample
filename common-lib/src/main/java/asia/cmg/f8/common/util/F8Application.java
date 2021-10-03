package asia.cmg.f8.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.TimeZone;

/**
 * An utility class to build spring boot application which some of customized setting
 * Created on 1/4/17.
 */
public final class F8Application {

    public static final Logger LOGGER = LoggerFactory.getLogger(F8Application.class);

    private SpringApplicationBuilder springBuilder;
    private final String name;

    public F8Application(final String name) {
        this.name = name;
    }


    public F8Application with(final SpringApplicationBuilder applicationBuilder) {
        this.springBuilder = applicationBuilder;
        return this;
    }

    public void run(final String... args) {
        if (springBuilder == null) {
            throw new IllegalStateException("Failed to run F8 application with empty spring builder");
        }

        configureTimeZone();

        // run app
        springBuilder.run(args);

        LOGGER.info("Run {} application...", name);
    }

    private void configureTimeZone() {
        final String timezone = System.getenv("user_timezone");
        if (timezone != null) {
            TimeZone.setDefault(TimeZone.getTimeZone(timezone));
            LOGGER.info("Updated user timezone to {}. Current user timezone is {}", timezone, TimeZone.getDefault());
        } else {
            LOGGER.info("user_timezone environment is not found. Use zone {} as default.", TimeZone.getDefault());
        }
    }
}

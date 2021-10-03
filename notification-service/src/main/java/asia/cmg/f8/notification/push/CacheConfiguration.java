package asia.cmg.f8.notification.push;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2/7/17.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(CacheConfiguration.class);

    @Autowired
    private CacheProperties cacheProperties;

    @Bean
    public CacheManager caffeineCacheManager() {
        final CaffeineCacheManager cacheManager = new CaffeineCacheManager("userLocaleCache");
        cacheManager.setAllowNullValues(false);

        final int maximumSize = cacheProperties.getMaximumSize();
        final int expireInSeconds = cacheProperties.getExpireInSeconds();

        cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(maximumSize).weakKeys().expireAfterAccess(expireInSeconds, TimeUnit.SECONDS));

        LOGGER.info("Configured userLocaleCache with maximumSize={} and expireInSeconds={}", maximumSize, expireInSeconds);

        return cacheManager;
    }
}

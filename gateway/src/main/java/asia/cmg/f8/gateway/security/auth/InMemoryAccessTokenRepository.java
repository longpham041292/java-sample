package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * Created on 10/21/16.
 */
public class InMemoryAccessTokenRepository implements AccessTokenRepository, InitializingBean {

    public static final Logger LOGGER = LoggerFactory.getLogger(InMemoryAccessTokenRepository.class);
    public static final String TOKEN_CACHE_NAME = "tokenCache";

    private final HazelcastInstance hazelcastInstance;
    private IMap<String, Authentication> cache;

    public InMemoryAccessTokenRepository(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void save(final String token, final Authentication authentication) {
        if (authentication != null) {
            cache.putIfAbsent(token, authentication);
        }
    }

    @Override
    public Authentication load(final String token) {
        return cache.get(token);
    }

    @Override
    public boolean hasAuthentication(final String token) {
        return cache.get(token) != null;
    }

    @Override
    public void remove(final String token) {
        cache.remove(token);
    }

    @Override
    public String findByUuid(final String uuid) {

        final Map.Entry<String, Authentication> auth = cache.entrySet().stream().filter(entry -> {
            final String principle = (String) entry.getValue().getPrincipal();
            return uuid.equals(principle);
        }).findFirst().orElse(null);

        if (auth != null) {
            return auth.getKey();
        }
        return null;
    }

    @Override
    @SuppressWarnings("PMD")
    public void afterPropertiesSet() throws Exception {

        final IMap<String, Authentication> tokenCache = hazelcastInstance.getMap(TOKEN_CACHE_NAME);
        if (tokenCache == null) {
            LOGGER.error("Cache key {} is not found. Failed to start", TOKEN_CACHE_NAME);
            throw new BeanInitializationException("Not found cache configuration for token cache");
        }
        this.cache = tokenCache;
    }
}

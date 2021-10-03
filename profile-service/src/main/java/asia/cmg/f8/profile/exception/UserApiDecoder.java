package asia.cmg.f8.profile.exception;

import asia.cmg.f8.common.exception.UserGridException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 10/27/16.
 */
public class UserApiDecoder implements ErrorDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(UserApiDecoder.class);
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(final String methodKey, final Response response) {
        LOG.error("User Grid error status {}", response.status());
        try {
            if (response.body() == null) {
                return null;
            }
            final HashMap<String, Object> result =
                    new ObjectMapper().readValue(Util.toString(response.body().asReader()), HashMap.class);
            LOG.error("User Grid error {}", result);
            final String error;
            if (result.get("error") instanceof Map) {
                error = (String) ((Map) result.get("error")).get("message");
            } else {
                error = (String) result.get("error");
            }

            final String detail = result.get("error_description") == null ? error : (String) result.get("error_description");

            if (response.status() >= 400 && response.status() <= 499 && response.body() != null) {
                throw new UserGridException(error, detail);
            }
        } catch (final IOException ioe) {
            throw (UserGridException) new UserGridException("Response data error", "Failed to process response body.").initCause(ioe);
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}

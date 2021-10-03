package asia.cmg.f8.gateway.security.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 12/2/16.
 */
public class ForwardedHeader {

    private static final ForwardedHeader NO_HEADER = new ForwardedHeader(Collections.<String, String>emptyMap());

    private final Map<String, String> elements;

    public ForwardedHeader(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static ForwardedHeader from(final String source) {

        if (!StringUtils.hasText(source)) {
            return NO_HEADER;
        }

        final Map<String, String> elements = new HashMap<String, String>();

        for (final String part : source.split(";")) {

            final String[] keyValue = part.split("=");

            if (keyValue.length != 2) {
                continue;
            }

            elements.put(keyValue[0].trim(), keyValue[1].trim());
        }

        Assert.notNull(elements, "Forwarded elements must not be null!");
        Assert.isTrue(!elements.isEmpty(), "At least one forwarded element needs to be present!");

        return new ForwardedHeader(elements);
    }

    public String getProto() {
        return elements.get("proto");
    }

    public void setProto(final String value) {
        elements.put("proto", value);
    }

    public String getHost() {
        return elements.get("host");
    }

    @Override
    public String toString() {
        return elements.entrySet().stream().map((entry) -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(";"));
    }
}

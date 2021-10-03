package asia.cmg.f8.common.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 11/11/16.
 */
public interface Localizable {

    @JsonProperty("localized_data")
    @Nullable
    Map<Locale, Map<String, String>> getLocalizedData();
}

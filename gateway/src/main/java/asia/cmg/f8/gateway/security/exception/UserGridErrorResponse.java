package asia.cmg.f8.gateway.security.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created on 1/16/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGridErrorResponse implements Serializable {

    private static final long serialVersionUID = -8250181771117810930L;

    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String description;

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

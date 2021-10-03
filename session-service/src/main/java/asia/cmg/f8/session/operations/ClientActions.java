package asia.cmg.f8.session.operations;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/24/16.
 */
public enum ClientActions {

    @JsonProperty("book")
    RESERVE("book"),

    @JsonProperty("checkin")
    CHECKIN("checkin"),

    @JsonProperty("noshow")
    NOSHOW("noshow"),

    @JsonProperty("decline")
    DECLINE("decline"),

    @JsonProperty("accept")
    ACCEPT("accept"),

    @JsonProperty("cancel")
    CANCEL("cancel");

    private final String description;

    ClientActions(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}

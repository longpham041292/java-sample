package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nhieu on 8/9/17.
 */
public enum ChatMessageType {

    @JsonProperty("text")
    TEXT,

    @JsonProperty("image")
    IMAGE,

    @JsonProperty("video")
    VIDEO;
}

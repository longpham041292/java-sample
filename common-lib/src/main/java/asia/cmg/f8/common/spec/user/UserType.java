package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import static asia.cmg.f8.common.util.CommonConstant.EU_USER_TYPE;
import static asia.cmg.f8.common.util.CommonConstant.PT_USER_TYPE;
import static asia.cmg.f8.common.util.CommonConstant.CLUB_USER_TYPE;

/**
 * Created on 10/23/16.
 */
public enum UserType {
    @JsonProperty(PT_USER_TYPE)
    PT,
    
    @JsonProperty(EU_USER_TYPE)
    EU,
    
    @JsonProperty(CLUB_USER_TYPE)
    CLUB
}

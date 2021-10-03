package asia.cmg.f8.notification.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 4/18/17.
 */
@FeignClient(value = "session", url = "${feign.session}", fallback = SessionNotificationClientFallback.class)
public interface SessionNotificationClient {

    @RequestMapping(path = "/notification/sessions", produces = APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    List<SessionNotificationInfo> getEligibleSessions(@RequestParam("fromTime") final long fromTime, @RequestParam("toTime") final long toTime);

    class SessionNotificationInfo {

        @JsonProperty("user_uuid")
        private String userUuid;

        @JsonProperty("pt_name")
        private String ptName;

        @JsonProperty("pt_uuid")
        private String ptUuid;

        @JsonProperty("start_time")
        private long startTime;

        @JsonProperty("session_uuid")
        private String sessionId;

        public String getUserUuid() {
            return userUuid;
        }

        public void setUserUuid(final String userUuid) {
            this.userUuid = userUuid;
        }

        public String getPtUuid() {
            return ptUuid;
        }

        public void setPtUuid(final String ptUuid) {
            this.ptUuid = ptUuid;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(final long startTime) {
            this.startTime = startTime;
        }

        public String getPtName() {
            return ptName;
        }

        public void setPtName(final String ptName) {
            this.ptName = ptName;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(final String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final SessionNotificationInfo info = (SessionNotificationInfo) obj;
            return getSessionId().equals(info.getSessionId());
        }

        @Override
        public int hashCode() {
            return getSessionId().hashCode();
        }

        @Override
        public String toString() {
            return "SessionNotificationInfo{" +
                    "sessionId='" + sessionId + '\'' +
                    ", userUuid='" + userUuid + '\'' +
                    ", ptName='" + ptName + '\'' +
                    ", ptUuid='" + ptUuid + '\'' +
                    ", startTime=" + startTime +
                    '}';
        }
    }
}

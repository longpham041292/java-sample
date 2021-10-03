package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.session.dto.SessionNotificationInfo;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.service.SessionNotificationService;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 4/18/17.
 */
@Service
public class InternalSessionNotificationService implements SessionNotificationService {

    private static final String QUERY;

    // @formatter:off
    static {
        QUERY = "SELECT s.uuid, s.user_uuid, s.pt_uuid, UNIX_TIMESTAMP(s.start_time) as start_time, u.full_name " +
                "FROM session_sessions AS s LEFT JOIN session_users AS u ON s.pt_uuid = u.uuid " +
                "WHERE s.status = :status AND UNIX_TIMESTAMP(s.start_time) BETWEEN :startTime AND :endTime";
    }
    // @formatter:on

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InternalSessionNotificationService(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SessionNotificationInfo> searchForNotification(final long fromTime, final long toTime) {

        final Map<String, Object> params = new HashMap<>(3);
        params.put("status", SessionStatus.CONFIRMED.name());
        params.put("startTime", fromTime);
        params.put("endTime", toTime);

        return jdbcTemplate.query(QUERY, params, (result, rowNum) -> {
            final SessionNotificationInfo sessionInfo = new SessionNotificationInfo();
            sessionInfo.setPtUuid(result.getString("pt_uuid"));
            sessionInfo.setSessionId(result.getString("uuid"));
            sessionInfo.setPtName(result.getString("full_name"));
            sessionInfo.setUserUuid(result.getString("user_uuid"));
            sessionInfo.setStartTime(result.getLong("start_time"));
            return sessionInfo;
        });
    }
}

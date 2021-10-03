package asia.cmg.f8.session.service;

import asia.cmg.f8.session.entity.SessionHistoryEntity;

import java.util.List;

/**
 * Created on 12/21/16.
 */
public interface SessionHistoryManagementService {

    SessionHistoryEntity save(SessionHistoryEntity entity);

    List<SessionHistoryEntity> save(List<SessionHistoryEntity> entities);

}

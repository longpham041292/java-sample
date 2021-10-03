package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.session.entity.SessionHistoryEntity;
import asia.cmg.f8.session.repository.SessionHistoryRepository;
import asia.cmg.f8.session.service.SessionHistoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 12/22/16.
 */
@Service
public class InternalSessionHistoryManagementService implements SessionHistoryManagementService {

    private final SessionHistoryRepository sessionHistoryRepository;

    @Autowired
    public InternalSessionHistoryManagementService(final SessionHistoryRepository sessionHistoryRepository) {
        this.sessionHistoryRepository = sessionHistoryRepository;
    }

    @Override
    public SessionHistoryEntity save(final SessionHistoryEntity entity) {
        return sessionHistoryRepository.save(entity);
    }

    @Override
    public List<SessionHistoryEntity> save(final List<SessionHistoryEntity> entities) {
        return sessionHistoryRepository.save(entities);
    }
}

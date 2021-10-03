package asia.cmg.f8.session.service;

import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.SessionEntity;

/**
 * Created on 12/22/16.
 */
public final class BookingInfo {

    private final EventEntity userEventEntity;
    private final EventEntity ptEventEntity;
    private final SessionEntity sessionEntity;

    public BookingInfo(final EventEntity userEventEntity, final EventEntity ptEventEntity, final SessionEntity sessionEntity) {
        this.userEventEntity = userEventEntity;
        this.ptEventEntity = ptEventEntity;
        this.sessionEntity = sessionEntity;
    }

    public EventEntity getUserEventEntity() {
        return userEventEntity;
    }

    public EventEntity getPtEventEntity() {
        return ptEventEntity;
    }

    public SessionEntity getSessionEntity() {
        return sessionEntity;
    }
}

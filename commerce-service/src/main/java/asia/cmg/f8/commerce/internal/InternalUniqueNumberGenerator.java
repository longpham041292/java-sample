package asia.cmg.f8.commerce.internal;

import asia.cmg.f8.commerce.entity.CounterEntity;
import asia.cmg.f8.commerce.repository.CounterRepository;
import asia.cmg.f8.commerce.service.UniqueNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created on 12/30/16.
 */
@Component
public class InternalUniqueNumberGenerator implements UniqueNumberGenerator {

    private final CounterRepository counterRepository;

    @Autowired
    public InternalUniqueNumberGenerator(final CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    @Override
    public int incrementAndGet(final String counterName) {
        CounterEntity counter = counterRepository.findOne(counterName);
        if (counter == null) {
            counter = new CounterEntity();
            counter.setName(counterName);
            counter.setValue(0); // default value for counter.
        }

        // increase counter value
        counter.setValue(counter.getValue() + 1);

        // then update
        counterRepository.saveAndFlush(counter);

        return counter.getValue();
    }
}

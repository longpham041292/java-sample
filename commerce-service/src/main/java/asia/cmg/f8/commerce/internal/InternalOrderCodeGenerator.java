package asia.cmg.f8.commerce.internal;

import asia.cmg.f8.commerce.service.OrderCodeGenerator;
import asia.cmg.f8.commerce.service.UniqueNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created on 11/18/16.
 */
@Component
public class InternalOrderCodeGenerator implements OrderCodeGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(InternalOrderCodeGenerator.class);

    public static final String ORDER_CODE_COUNTER_NAME = "order_code";

    private final UniqueNumberGenerator numberGenerator;

    public InternalOrderCodeGenerator(final UniqueNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Override
    public String generate() {

        final String code = OrderCodeUtil.build(numberGenerator.incrementAndGet(ORDER_CODE_COUNTER_NAME));

        LOGGER.info("Generate order code {}", code);

        return code;
    }
}

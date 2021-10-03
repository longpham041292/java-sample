package asia.cmg.f8.commerce.service;

/**
 * Created on 12/30/16.
 */
@FunctionalInterface
public interface UniqueNumberGenerator {

    int incrementAndGet(String counterName);
}

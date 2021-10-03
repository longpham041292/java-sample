package asia.cmg.f8.commerce.service;

/**
 * Created on 11/18/16.
 */
@FunctionalInterface
public interface OrderCodeGenerator {

    /**
     * Generate an unique order code with 6 digits as maximum.
     *
     * @return an unique order code.
     */
    String generate();
}

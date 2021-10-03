package asia.cmg.f8.common.context;

/**
 * @author tung.nguyenthanh
 */
public interface LanguageContext {

    /**
     * Return prefer language for current request.
     * Language is resolve as the belowing order:
     * 1. Request parameter "lang"
     * 2. Language set in the account
     * 3. Default language: en
     *
     * @return the current Locale, or {@code null} if no specific Locale associated
     */
    String language();
}

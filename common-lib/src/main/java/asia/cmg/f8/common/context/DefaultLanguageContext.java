package asia.cmg.f8.common.context;


public class DefaultLanguageContext implements LanguageContext {

    private final String lang;

    public DefaultLanguageContext(final String lang) {
        super();
        this.lang = lang;
    }

    @Override
    public String language() {
        return lang;
    }

}

package asia.cmg.f8.session.utils;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

/**
 * Created on 11/25/16.
 */
public class CaseInsensitiveConverter<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> typeParameterClass;

    public CaseInsensitiveConverter(final Class<T> typeParameterClass) {
        super();
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        final String upper = text.toUpperCase(Locale.getDefault()); // or something more robust
        final T value = T.valueOf(typeParameterClass, upper);
        setValue(value);
    }
}

package asia.cmg.f8.commerce.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	private final MessageSource messageSource;

	public MessageService() {
		final ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("i18n/messages");
		resourceBundleMessageSource.setDefaultEncoding("utf-8");
		this.messageSource = resourceBundleMessageSource;
	}

	public String getMessage(final String code, final Locale locale, String defaultMessage) {
		return messageSource.getMessage(code, null, defaultMessage, locale);
	}

}

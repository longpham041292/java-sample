package asia.cmg.f8.notification.service.email;

import org.apache.commons.lang.CharEncoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSenderImpl javaMailSender;

    private final SpringTemplateEngine templateEngine;

    private final MessageSource messageSource;

    @Inject
    public MailService(final JavaMailSenderImpl javaMailSender,
            final SpringTemplateEngine templateEngine, final MessageSource messageSource) {
        super();
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    @Async
    public void sendEmail(final String fromEmail, final String toEmail, final String subject,
            final String content, final boolean isMultipart, final boolean isHtml) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart,
                    CharEncoding.UTF_8);
            message.setFrom(fromEmail);
            message.setTo(InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            LOG.info("Sent e-mail to User '{}'", toEmail);
        } catch (final Exception exp) {
            LOG.warn("Error on sending email to user", exp);
        }
    }

    @Async
    public void sendEmail(final String fromEmail, final String toEmail, final String emailTemplate,
            final String titleMessage, final Context context, final boolean isMultipart) {
        final String content = templateEngine.process(emailTemplate, context);
        final String subject = messageSource.getMessage(titleMessage, null, context.getLocale());
        sendEmail(fromEmail, toEmail, subject, content, isMultipart, true);
    }
}

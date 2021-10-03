package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.notification.client.SessionClient;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.session.TransferSessionPackageEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

@Service
public class TransferSessionPackageConfirmationEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferSessionPackageConfirmationEmailService.class);

    private static final String EU_TRANSFER_CONFIRM_TEMPLATE = "EUTransferSessionPackageConfirmationEmailTemplate";
    private static final String OLD_PT_TRANSFER_CONFIRM_TEMPLATE = "OldPTTransferSessionPackageConfirmationEmailTemplate";
    private static final String NEW_PT_TRANSFER_CONFIRM_TEMPLATE = "NewPTTransferSessionPackageConfirmationEmailTemplate";
    private static final String TRANSFER_CONFIRM_TITLE = "email.transfer.confirm.title";
    public static final String NOT_STARTED = "NOT STARTED";

    private final MailService mailService;
    private final SessionClient sessionClient;
    private final UserClient userClient;
    private final NotificationProperties notificationProps;
    private final MailAssetProperties mailAssetProperties;

    public TransferSessionPackageConfirmationEmailService(final MailService mailService,
                                                          final SessionClient sessionClient,
                                                          final UserClient userClient,
                                                          final NotificationProperties notificationProps,
                                                          final MailAssetProperties mailAssetProperties) {
        this.mailService = mailService;
        this.sessionClient = sessionClient;
        this.userClient = userClient;
        this.notificationProps = notificationProps;
        this.mailAssetProperties = mailAssetProperties;
    }

    public void handle(final TransferSessionPackageEvent message) {

        final UserGridResponse<UserEntity> endUserResp = userClient.getUserByUuid(message.getUserUuid().toString());
        if (endUserResp.getEntities().isEmpty()) {
            LOGGER.error("End user or pt not found {}", message.getUserUuid());
            return;
        }

        final CurrentTransferPackageInfo transferSessionPackageInfo = sessionClient.getTransferSessionPackageInfo(
                message.getOldSessionPackageUuid().toString(),
                message.getNewSessionPackageUuid().toString());
        if (Objects.isNull(transferSessionPackageInfo)) {
            LOGGER.error("Can not find transfer session package info for old package {} and new package {}",
                    message.getOldSessionPackageUuid().toString(), message.getNewSessionPackageUuid().toString());
            return;
        }
        final Long createdDate = transferSessionPackageInfo.getCreatedDate();
        final Long modifiedDate = transferSessionPackageInfo.getModifiedDate();
        //When server is deployed, it trigger sending old email. This condition make sure fire the event in today
        if(isToday(createdDate) || isToday(modifiedDate)) {

	        final UserEntity user = endUserResp.getEntities().get(0);
	        final Locale locale = getUserLocale(user);
	        final Context context = new Context(locale);
	        context.setVariable("endUserName", transferSessionPackageInfo.getUserFullName());
	        context.setVariable("endUserId", transferSessionPackageInfo.getUserUuid());
	        context.setVariable("newTrainerName", transferSessionPackageInfo.getNewTrainerFullName());
	        context.setVariable("oldTrainerName", transferSessionPackageInfo.getOldTrainerFullName());
	
	        final int numberOfTransferred = transferSessionPackageInfo.getNumberOfTotalSessions() - transferSessionPackageInfo.getNumberOfBurnedSessions();
	        context.setVariable("numberOfTransferredSessions", numberOfTransferred);
	        context.setVariable("numberOfTotalSessions", transferSessionPackageInfo.getNumberOfTotalSessions());
	
	        if (Objects.isNull(transferSessionPackageInfo.getOrderExpiredDate())) {
	            context.setVariable("expiredDate", NOT_STARTED);
	        } else {
	            final String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
	                    FormatStyle.SHORT, null, IsoChronology.INSTANCE,
	                    locale);
	            final String orderExpiredDate = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(transferSessionPackageInfo.getOrderExpiredDate())
	                    .format(DateTimeFormatter.ofPattern(datePattern));
	            context.setVariable("expiredDate", orderExpiredDate);
	        }
	
	        context.setVariable("logoUrl", mailAssetProperties.getLogoUrl());
	        context.setVariable("spacerUrl", mailAssetProperties.getSpacerUrl());
	
	        //Send mail for end user
	        mailService.sendEmail(notificationProps.getMail().getFrom(), user.getEmail(),
	                EU_TRANSFER_CONFIRM_TEMPLATE, TRANSFER_CONFIRM_TITLE, context, false);
	
	        //Send mail for old trainer user
	        mailService.sendEmail(notificationProps.getMail().getFrom(), transferSessionPackageInfo.getOldTrainerEmail(),
	                OLD_PT_TRANSFER_CONFIRM_TEMPLATE, TRANSFER_CONFIRM_TITLE, context, false);
	
	        //Send mail for new trainer user
	        mailService.sendEmail(notificationProps.getMail().getFrom(), transferSessionPackageInfo.getNewTrainerEmail(),
	                NEW_PT_TRANSFER_CONFIRM_TEMPLATE, TRANSFER_CONFIRM_TITLE, context, false);
        }
    }

    private Locale getUserLocale(final UserEntity user) {
        final String language = StringUtils.isEmpty(user.getLanguage()) ? notificationProps
                .getDefaultLang() : user.getLanguage();
        return Locale.forLanguageTag(language);
    }
    
    private  boolean isToday(final Long timestamp) {
    	LOGGER.info("-----------------Timestamp checking {} ", timestamp );
    	if(null == timestamp) {
    		return false;
    	}
        final Calendar now = Calendar.getInstance();
        final Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        LOGGER.info("-----------------Timestamp Year {} , and Timestamp dayOfyear {}, Now Year {}, Now dayOfYear {}  ", timeToCheck.get(Calendar.YEAR), 
        		timeToCheck.get(Calendar.DAY_OF_YEAR), now.get(Calendar.YEAR), now.get(Calendar.DAY_OF_YEAR));
        return (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR));
    } 

}

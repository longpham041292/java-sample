package asia.cmg.f8.notification.service.email;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.notification.client.CommerceClient;
import asia.cmg.f8.notification.client.UserClient;
import asia.cmg.f8.notification.config.MailAssetProperties;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.dto.Order;
import asia.cmg.f8.notification.entity.UserEntity;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.util.DateTimeUtils;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class OrderConfirmationEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderConfirmationEmailService.class);

    private static final String ORDER_CONFIRM_TEMPLATE = "OrderConfirmationEmailTemplate";
    private static final String ORDER_CONFIRM_TITLE = "email.order.confirm.title";

    private final MailService mailService;
    private final CommerceClient commerceClient;
    private final UserClient userClient;
    private final NotificationProperties notificationProps;
    private final MailAssetProperties mailAssetProperties;

    public OrderConfirmationEmailService(final MailService mailService,
                                         final CommerceClient commerceClient,
                                         final UserClient userClient,
                                         final NotificationProperties notificationProps,
                                         final MailAssetProperties mailAssetProperties) {
        this.mailService = mailService;
        this.commerceClient = commerceClient;
        this.userClient = userClient;
        this.notificationProps = notificationProps;
        this.mailAssetProperties = mailAssetProperties;
    }

    public void handle(final OrderCompletedEvent message) {
        final String orderUuid = message.getOrderId().toString();
        if (StringUtils.isEmpty(orderUuid)) {
            LOG.error("Order Id is missing");
            return;
        }

        final Order order = commerceClient.getOrder(orderUuid);
        if (order == null) {
            LOG.error("Order Id is not found {}", orderUuid);
            return;
        }
        final UserGridResponse<UserEntity> endUserResp = userClient.getUserByUuid(order
                .getUserUuid());
        final UserGridResponse<UserEntity> ptResp = userClient.getUserByUuid(order.getPtUuid());
        if (endUserResp.getEntities().isEmpty() || ptResp.getEntities().isEmpty()) {
            LOG.error("End user or pt not found {}, {}", order.getUserUuid(), order.getPtUuid());
            return;
        }

        final UserEntity user = endUserResp.getEntities().get(0);
        final UserEntity ptUser = ptResp.getEntities().get(0);
        final Locale locale = getUserLocale(user);
        final Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("ptUser", ptUser);
        context.setVariable("order", order);
        context.setVariable("product", order.getProducts().get(0));

        final String displaySubTotal = CurrencyUtils.formatCurrency(order.getCurrency(),
                locale.getLanguage(), order.getSubTotal());
        final String displayTotalPrice = CurrencyUtils.formatCurrency(order.getCurrency(),
                locale.getLanguage(), order.getTotalPrice());
        final Map<String, String> formatData = new HashMap<>();
        formatData.put("subTotal", displaySubTotal);
        formatData.put("totalPrice", displayTotalPrice);
        if(order.getFreeOrder()){
            final LocalDate expireDate = LocalDate.now().plusDays(order.getProducts().get(0).getExpireLimit());
            formatData.put("expireDate", DateTimeUtils.formatDate(expireDate));
        }
        
        context.setVariable("formatData", formatData);
        context.setVariable("termUrl", notificationProps.getTermCondUrl());
        context.setVariable("logoUrl", mailAssetProperties.getLogoUrl());
        context.setVariable("spacerUrl", mailAssetProperties.getSpacerUrl());


        mailService.sendEmail(notificationProps.getMail().getFrom(), user.getEmail(),
                ORDER_CONFIRM_TEMPLATE, ORDER_CONFIRM_TITLE, context, false);
    }

    private Locale getUserLocale(final UserEntity user) {
        final String language = StringUtils.isEmpty(user.getLanguage()) ? notificationProps
                .getDefaultLang() : user.getLanguage();
        return Locale.forLanguageTag(language);
    }

}

package asia.cmg.f8.session.internal.service;

import org.springframework.stereotype.Service;

import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.service.EventManagementService;
import asia.cmg.f8.session.service.EventService;
import asia.cmg.f8.session.service.OrderManagementService;
import asia.cmg.f8.session.service.SessionHistoryManagementService;
import asia.cmg.f8.session.service.SessionManagementService;
import asia.cmg.f8.session.service.SessionService;
import asia.cmg.f8.session.service.ValidationService;

/**
 * Created on 2/16/17.
 */
@Service(OnBehalfSessionBookingService.SERVICE_ID)
public class OnBehalfSessionBookingService extends AbstractSessionBookingService {

	public static final String SERVICE_ID = "onBehalfSessionBookingService";

	public OnBehalfSessionBookingService(final SessionManagementService sessionManagementService,
			final SessionHistoryManagementService sessionHistoryManagementService, final SessionService sessionService,
			final EventManagementService eventManagementService, final ValidationService validationService,
			final EventHandler eventHandler, final OrderManagementService orderManagementService,
			final EventService eventService) {
		super(sessionManagementService, sessionHistoryManagementService, sessionService, eventManagementService,
				validationService, eventHandler, orderManagementService, eventService);
	}

}

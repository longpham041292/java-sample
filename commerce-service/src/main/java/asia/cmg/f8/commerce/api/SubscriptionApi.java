package asia.cmg.f8.commerce.api;

import asia.cmg.f8.commerce.client.UserClient;
import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.dto.SubscriptionDto;
import asia.cmg.f8.commerce.dto.SubscriptionTypeDto;
import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.commerce.entity.OrderOptionEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.service.OrderOptionService;
import asia.cmg.f8.commerce.service.SubscriptionService;
import asia.cmg.f8.commerce.service.SubscriptionTypeService;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.UserGridResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import javax.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
public class SubscriptionApi {

    private static final String UUID = "uuid";
    private static final String SUCCESS = "success";
    private final Logger LOGGER = LoggerFactory.getLogger(SubscriptionApi.class);
    private final Gson gson = new Gson();

    @Autowired
    private SubscriptionTypeService subscriptionTypeService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private CommerceProperties commerceProps;

    @Autowired
    private UserClient userClient;
    
    @Autowired
    private OrderOptionService orderOptionService;

    /**
     * Create new subscription type
     *
     * @param dto SubscriptionTypeDto
     * @return
     */
    @RequestMapping(value = "/admin/subscriptions/types", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionTypeDto> createSubscriptionType(@RequestBody @Valid final SubscriptionTypeDto dto) {

    	LOGGER.info("Logging input for createSubscriptionType: {}", gson.toJson(dto));
    	
        return new ResponseEntity(subscriptionTypeService.createSubscriptionType(dto, commerceProps.getCountry(),
                commerceProps.getCurrency()), HttpStatus.OK);
    }

    /**
     * Get list subscription type
     *
     * @return List<SubscriptionTypeDto>
     */
    @RequestMapping(value = "/admin/subscriptions/types", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<SubscriptionTypeDto>> findAllSubscriptionTypes() {
        return new ResponseEntity(subscriptionTypeService.getAllSubscriptionType(), HttpStatus.OK);
    }

    /**
     * Get detail subscription type
     *
     * @param stUuid
     * @return SubscriptionTypeDto
     */
    @RequestMapping(value = "/admin/subscriptions/types/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionTypeDto> getSubscriptionType(@PathVariable(UUID) final String stUuid) {
        return new ResponseEntity(subscriptionTypeService.getSubscriptionTypeByUuid(stUuid), HttpStatus.OK);
    }

    /**
     * Update subscription type
     * @param stUuid
     * @param dto
     * @return
     */
    @RequestMapping(value = "/admin/subscriptions/types/{uuid}", method = RequestMethod.PUT,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionTypeDto> updateSubScriptionType(@PathVariable(name = UUID, required = true) final String stUuid,
                                                                	  @RequestBody @Valid final SubscriptionTypeDto dto) {
        try {
        	LOGGER.info("Logging input for updateSubScriptionType: {}", gson.toJson(dto));
        	
			return new ResponseEntity(subscriptionTypeService.updateSubscriptionType(stUuid, dto), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    /**
     * Create new subscription
     *
     * @param dto SubscriptionDto
     * @return
     */
    @RequestMapping(value = "/admin/subscriptions", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionDto> createSubscription(@RequestBody @Valid final SubscriptionDto dto) {
    	
    	LOGGER.info("Logging input for createSubscription: {}", gson.toJson(dto));
    	
        if (!subscriptionService.checkSubscriptionTypeExist(dto.getSubscriptionTypeId())) {
        	return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(subscriptionService.createSubscription(dto, commerceProps.getCountry(),commerceProps.getCurrency()), HttpStatus.OK);
    }

    /**
     * Get list subscription
     *
     * @return List<SubscriptionDto>
     */
    @RequestMapping(value = "/admin/subscriptions", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<SubscriptionDto>> findAllSubscriptions() {
        return new ResponseEntity(subscriptionService.getAllSubscriptions(), HttpStatus.OK);
    }

    /**
     * Get detail subscription
     *
     * @param sUuid
     * @return SubscriptionDto
     */
    @RequestMapping(value = "/admin/subscriptions/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionDto> getSubscription(@PathVariable(UUID) final String sUuid) {
        return new ResponseEntity(subscriptionService.getSubscriptionDtoByUuid(sUuid), HttpStatus.OK);
    }

    /**
     * Get detail subscriptions
     *
     * @param userUuid
     * @return SubscriptionDto
     */
    @RequestMapping(value = "/subscriptions/users/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SubscriptionDto>> getSubscriptionByUserUuid(@PathVariable(UUID) final String userUuid) {
        UserGridResponse<UserEntity> resp = userClient.getUser(userUuid);
        if (resp.getEntities().isEmpty()) {
            throw new IllegalArgumentException("User is invalid");
        }

        String level = resp.getEntities().get(0).getLevel();
        if (StringUtils.isEmpty(level)) {
            throw new IllegalArgumentException("User level not found");
        }
        return new ResponseEntity(subscriptionService.getSubscriptionByUserLevel(level.toUpperCase(Locale.ENGLISH),
                commerceProps.getCountry()), HttpStatus.OK);
    }
    
    /**
     * @author thach vo
     * Get subscriptions by PT's level and optionId
     * @param userUuid
     * @param optionId
     * @return List<SubscriptionDto>
     */
    @RequestMapping(value = "/subscriptions/users/{uuid}/options/{option_id}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SubscriptionDto>> getSubscriptionByOptionId(@PathVariable(UUID) final String userUuid,
																		   @PathVariable("option_id") final Integer optionId) {
        UserGridResponse<UserEntity> resp = userClient.getUser(userUuid);
        if (resp.getEntities().isEmpty()) {
            throw new IllegalArgumentException("User is invalid");
        }

        String level = resp.getEntities().get(0).getLevel();
        if (StringUtils.isEmpty(level)) {
            throw new IllegalArgumentException("User level not found");
        }
        return new ResponseEntity(subscriptionService.
        						getSubscriptionByUserLevelAndOptionId(commerceProps.getCountry(), level.toUpperCase(Locale.ENGLISH), optionId), HttpStatus.OK);
    }

    /**
     * Update subscription
     * @param sUuid
     * @param dto
     * @return
     */
    @RequestMapping(value = "/admin/subscriptions/{uuid}", method = RequestMethod.PUT,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable(UUID) final String sUuid,
                                                                @RequestBody @Valid final SubscriptionDto dto) {
    	
    	LOGGER.info("Logging input for updateSubscription: {}", gson.toJson(dto));
    	
        if (!subscriptionService.checkSubscriptionTypeExist(dto.getSubscriptionTypeId())) {
        	return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(subscriptionService.updateSubscription(sUuid, dto), HttpStatus.OK);
    }
    
    /**
     * Get all order subscription options
     * Currently only for order_type = SUBSCRIPTION
     * @return
     */
    @GetMapping(value = "/admin/subscriptions/order/options", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OrderOptionEntity>> getAllOrderOptions() {
    	
    	try {
			return new ResponseEntity<List<OrderOptionEntity>>(orderOptionService.getByOrderType(OrderType.SUBSCRIPTION.name()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<OrderOptionEntity>>(HttpStatus.OK);
		}
    }
    
    /**
     * Get order subscription options by PT's level code
     * Currently only for order_type = SUBSCRIPTION
     * @return
     */
    @GetMapping(value = "/admin/subscriptions/order/options/{level}", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OrderOptionEntity>> getOrderOptionsByLevel(@PathVariable(name = "level", required = true) final String levelCode) {
    	
    	try {
			return new ResponseEntity<List<OrderOptionEntity>>(orderOptionService.getByOrderTypeAndLevel(OrderType.SUBSCRIPTION.name(), levelCode), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<OrderOptionEntity>>(HttpStatus.OK);
		}
    }
    
    @RequiredAdminRole
    @PostMapping(value = "/admin/subscriptions/order/options", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderOptionEntity> createOrderOption(@RequestBody @Valid final OrderOptionEntity orderOptionEntity) {
    	
    	try {
    		LOGGER.info("Logging input for createOrderOption: {}", gson.toJson(orderOptionEntity));
    		
			return new ResponseEntity<OrderOptionEntity>(orderOptionService.createOrderOption(orderOptionEntity), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<OrderOptionEntity>(HttpStatus.BAD_REQUEST);
		}
    }
    
    @RequiredAdminRole
    @PutMapping(value = "/admin/subscriptions/order/options", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateOrderOption(@RequestBody @Valid final OrderOptionEntity orderOptionEntity) {
    	
    	try {
    		LOGGER.info("Logging input for updateOrderOption: {}", gson.toJson(orderOptionEntity));
    		
			orderOptionService.updateOrderOption(orderOptionEntity);
			return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.FALSE), HttpStatus.OK);
		}
    }
}

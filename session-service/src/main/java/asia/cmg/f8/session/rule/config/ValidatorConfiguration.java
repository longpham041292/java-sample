package asia.cmg.f8.session.rule.config;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import asia.cmg.f8.session.rule.booking.BookInPastValidator;
import asia.cmg.f8.session.rule.booking.BookInPtAvailabilityValidator;
import asia.cmg.f8.session.rule.booking.BookOverPackageExpiredDateValidator;
import asia.cmg.f8.session.rule.booking.DeactivatedUserValidator;
import asia.cmg.f8.session.rule.booking.DoubleBookingValidator;
import asia.cmg.f8.session.rule.booking.NotValidTimeSlotValidator;
import asia.cmg.f8.session.rule.booking.OverlappingSessionValidator;
import asia.cmg.f8.session.rule.booking.ValidationStrategy;

/**
 * Created on 12/15/16.
 */
@Configuration
public class ValidatorConfiguration {


    @Bean
    public Set<ValidationStrategy> validationStrategies() {
        final Set<ValidationStrategy> strategies = new LinkedHashSet<>();
        strategies.add(bookInPastValidator());
        strategies.add(bookDeactivatedUser());
        strategies.add(bookInPtAvailabilityValidator());
        //strategies.add(bookWithinTimeValidator());
        strategies.add(bookOverPackageExpiredDateValidator());
        strategies.add(overlappingSessionValidator());
        strategies.add(doubleBookingValidator());
        strategies.add(notValidTimeSlotValidator());
        return strategies;
    }

    private BookInPastValidator bookInPastValidator() {
        return new BookInPastValidator();
    }

    private BookInPtAvailabilityValidator bookInPtAvailabilityValidator() {
        return new BookInPtAvailabilityValidator();
    }

//    private BookWithinTimeValidator bookWithinTimeValidator() {
//        return new BookWithinTimeValidator(validatorProperties);
//    }

    private DoubleBookingValidator doubleBookingValidator() {
        return new DoubleBookingValidator();
    }

    private NotValidTimeSlotValidator notValidTimeSlotValidator() {
        return new NotValidTimeSlotValidator();
    }

    private OverlappingSessionValidator overlappingSessionValidator() {
        return new OverlappingSessionValidator();
    }

    private BookOverPackageExpiredDateValidator bookOverPackageExpiredDateValidator() {
        return new BookOverPackageExpiredDateValidator();
    }

    private DeactivatedUserValidator bookDeactivatedUser() {
        return new DeactivatedUserValidator();
    }

}

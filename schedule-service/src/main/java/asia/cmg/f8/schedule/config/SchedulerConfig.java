	package asia.cmg.f8.schedule.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.sql.DataSource;

import asia.cmg.f8.schedule.jobs.*;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import asia.cmg.f8.schedule.notification.NotifyBeforeSessionStartJob;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
@SuppressWarnings("PMD.TooManyMethods")
public class SchedulerConfig {

    private static final String QUARTZ_CONFIG = "/quartz.properties";

    @Autowired
    private List<Trigger> listOfTrigger;

    @Bean
    public JobFactory jobFactory(final ApplicationContext applicationContext) {
        final AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(final DataSource dataSource, final JobFactory jobFactory) throws IOException {
        final SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());

        // Here we will set all the trigger beans we have defined.
        if (!listOfTrigger.isEmpty()) {
            factory.setTriggers(listOfTrigger.toArray(new Trigger[listOfTrigger.size()]));
        }

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_CONFIG));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    private SimpleTriggerFactoryBean createTrigger(final JobDetail jobDetail, final long pollFrequencyMs) {
        final SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    private CronTriggerFactoryBean createCronTrigger(final JobDetail jobDetail, final String cronExpression) {
        final CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

    private CronTriggerFactoryBean createCronTrigger(final JobDetail jobDetail, final String cronExpression, String timeZone) {
    	CronTriggerFactoryBean cronTrigger = createCronTrigger(jobDetail, cronExpression);
    	cronTrigger.setTimeZone(TimeZone.getTimeZone(timeZone));
        return cronTrigger;
    }

    private JobDetailFactoryBean createJobDetail(final Class jobClass) {
        final JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public JobDetailFactoryBean queryPaymentDetail() {
        return createJobDetail(QueryPaymentJob.class);
    }

    @Bean(name = "queryPaymentTrigger")
    public SimpleTriggerFactoryBean queryPaymentTrigger(
            @Qualifier("queryPaymentDetail") final JobDetail jobDetail,
            @Value("${jobs.paymentQueryJob.frequency}") final long frequency) {
        return createTrigger(jobDetail, frequency);
    }

    @Bean(name = "createSessionDailyViewJob")
    public JobDetailFactoryBean createSessionDailyViewJob() {
        return createJobDetail(SessionDailyViewJob.class);
    }

    @Bean(name = "sessionDailyViewTrigger")
    public CronTriggerFactoryBean sessionDailyViewTrigger(
            @Qualifier("createSessionDailyViewJob") final JobDetail jobDetail,
            @Value("${jobs.sessionDailyViewJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "createSessionAddAvailabilityJob")
    public JobDetailFactoryBean createSessionAddAvailabilityJob() {
        return createJobDetail(SessionAddAvailabilityJob.class);
    }

    @Bean(name = "createSessionAddAvailabilityTrigger")
    public CronTriggerFactoryBean createSessionAddAvailabilityTrigger(
            @Qualifier("createSessionAddAvailabilityJob") final JobDetail jobDetail,
            @Value("${jobs.sessionAddAvailabilityJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "createSessionRemoveOldAvailabilityJob")
    public JobDetailFactoryBean createSessionRemoveOldAvailabilityJob() {
        return createJobDetail(SessionRemoveOldAvailabilityJob.class);
    }

    @Bean(name = "createSessionRemoveOldAvailabilityTrigger")
    public CronTriggerFactoryBean createSessionRemoveOldAvailabilityTrigger(
            @Qualifier("createSessionRemoveOldAvailabilityJob") final JobDetail jobDetail,
            @Value("${jobs.sessionRemoveOldAvailabilityJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "createSessionStatsDailyViewJob")
    public JobDetailFactoryBean createSessionStatsDailyViewJob() {
        return createJobDetail(SessionStatsDailyViewJob.class);
    }

    @Bean(name = "sessionStatsDailyViewTrigger")
    public CronTriggerFactoryBean sessionStatsDailyViewTrigger(
            @Qualifier("createSessionStatsDailyViewJob") final JobDetail jobDetail,
            @Value("${jobs.sessionStatsDailyViewJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "createTrainerAnnualRevenueJob")
    public JobDetailFactoryBean createTrainerAnnualRevenueJob() {
        return createJobDetail(TrainerAnnualRevenueJob.class);
    }

    @Bean(name = "trainerAnnualRevenueTrigger")
    public CronTriggerFactoryBean trainerAnnualRevenueTrigger(
            @Qualifier("createTrainerAnnualRevenueJob") final JobDetail jobDetail,
            @Value("${jobs.trainerAnnualRevenueJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "orderReconcileDailyViewJob")
    public JobDetailFactoryBean orderReconcileDailyViewJob() {
        return createJobDetail(OrderReconcileDailyViewJob.class);
    }

    @Bean(name = "orderReconcileDailyViewTrigger")
    public CronTriggerFactoryBean orderReconcileDailyViewTrigger(
            @Qualifier("orderReconcileDailyViewJob") final JobDetail jobDetail,
            @Value("${jobs.orderReconcileDailyViewJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "notifyBeforeSessionStartJob")
    public JobDetailFactoryBean notifyBeforeSessionStartJob() {
        return createJobDetail(NotifyBeforeSessionStartJob.class);
    }

    @Bean(name = "notifyBeforeSessionStartJobTrigger")
    public CronTriggerFactoryBean notifyBeforeSessionStartJobTrigger(@Qualifier("notifyBeforeSessionStartJob") final JobDetail jobDetail,
                                                                     @Value("${jobs.notifyBeforeSessionStartJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

	@Bean(name = "createWhosHotAlgorithmRunJob")
	public JobDetailFactoryBean createWhosHotAlgorithmRunJob() {
		return createJobDetail(WhosHotAlgorithmRunJob.class);
	}

	@Bean(name = "whosHotAlgorithmRunJobTrigger")
	public CronTriggerFactoryBean whosHotAlgorithmRunJobTrigger(
			@Qualifier("createWhosHotAlgorithmRunJob") final JobDetail jobDetail,
			@Value("${jobs.whosHotPTSelectAlgorithmRunJob.cronExpression}") final String cronExpression) {
		return createCronTrigger(jobDetail, cronExpression);
	}

	@Bean(name = "createResetAutoFollowTriggerJob")
	public JobDetailFactoryBean createResetAutoFollowTriggerJob() {
		return createJobDetail(ResetAutoFollowJob.class);
	}

    @Bean(name = "resetAutoFollowTrigger")
    public CronTriggerFactoryBean resetAutoFollowTrigger(
            @Qualifier("createResetAutoFollowTriggerJob") final JobDetail jobDetail,
            @Value("${jobs.resetAutoFollowJob.cronExpression}") final String cronExpression,
            @Value("${jobs.resetAutoFollowJob.timeZone}") final String timeZone) {
        return createCronTrigger(jobDetail, cronExpression, timeZone);
    }

	@Bean(name = "sessionAutoBurnConfirmTriggerJob")
	public JobDetailFactoryBean sessionAutoBurnConfirmTriggerJob() {
		return createJobDetail(SessionAutoBurnConfirmJob.class);
	}

    @Bean(name = "sessionAutoBurnConfirmTrigger")
    public CronTriggerFactoryBean sessionAutoBurnConfirmTrigger(
    		@Qualifier("sessionAutoBurnConfirmTriggerJob") final JobDetail jobDetail,
            @Value("${jobs.autoBurnConfirmSessionJob.cronExpression}") final String cronExpression,
            @Value("${jobs.autoBurnConfirmSessionJob.timeZone}") final String timeZone) {
    	return createCronTrigger(jobDetail, cronExpression, timeZone);
    }

    @Bean(name = "expiredPurchasedCreditPackagesCheckingJob")
    public JobDetailFactoryBean expiredPurchasedCreditPackagesCheckingJob() {
    	return createJobDetail(ExpiredPurchasedCreditPackageCheckingJob.class);
    }

    @Bean(name = "expiredPurchasedCreditPackagesCheckingTrigger")
    public CronTriggerFactoryBean expiredPurchasedCreditPackagesCheckingTrigger(
    		@Qualifier("expiredPurchasedCreditPackagesCheckingJob") final JobDetail jobDetail,
    		@Value("${jobs.expiredPurchasedCreditPackagesCheckingJob.cronExpression}") final String cronExpression) {
    	return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoBurnConfirmedCreditSessionJob")
    public JobDetailFactoryBean autoBurnConfirmedCreditSessionJob() {
    	return createJobDetail(AutoBurningOfConfirmedCreditSessionJob.class);
    }

    @Bean(name = "autoBurnConfirmedCreditSessionJobTrigger")
    public CronTriggerFactoryBean autoBurnConfirmedCreditSessionJobTrigger(
    		@Qualifier("autoBurnConfirmedCreditSessionJob") final JobDetail jobDetail,
    		@Value("${jobs.autoBurnConfirmedCreditSessionJob.cronExpression}") final String cronExpression) {
    	return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoDeductBurnedCreditSessionJob")
    public JobDetailFactoryBean autoDeductBurnedCreditSessionJob() {
    	return createJobDetail(AutoDeductingOfBurnedCreditSessionJob.class);
    }

    @Bean(name = "autoDeductBurnedCreditSessionTrigger")
    public CronTriggerFactoryBean autoDeductBurnedCreditSessionTrigger(
    		@Qualifier("autoDeductBurnedCreditSessionJob") final JobDetail jobDetail,
    		@Value("${jobs.autoDeductBurnedCreditSessionJob.cronExpression}") final String cronExpression) {
    	return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoBurningClassBookingsJob")
    public JobDetailFactoryBean autoBurningClassBookingsJob() {
        return createJobDetail(AutoBurningClassBookingsJob.class);
    }

    @Bean(name = "autoBurningClassBookingsJobTrigger")
    public CronTriggerFactoryBean autoBurningClassBookingsJobTrigger(
            @Qualifier("autoBurningClassBookingsJob") final JobDetail jobDetail,
            @Value("${jobs.autoBurnClassBookingsJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoBurningEticketBookingsJob")
    public JobDetailFactoryBean autoBurningEticketBookingsJob() {
        return createJobDetail(AutoBurningEticketBookingsJob.class);
    }

    @Bean(name = "autoBurningEticketBookingsJobTrigger")
    public CronTriggerFactoryBean autoBurningEticketBookingsJobTrigger(
            @Qualifier("autoBurningEticketBookingsJob") final JobDetail jobDetail,
            @Value("${jobs.autoBurnEticketBookingsJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoCancelSessionBookingJob")
    public JobDetailFactoryBean autoCancelSessionBookingJob() {
        return createJobDetail(AutoCancelSessionBookingJob.class);
    }

    @Bean(name = "autoCancelSessionBookingJobTrigger")
    public CronTriggerFactoryBean autoCancelSessionBookingJobTrigger(
            @Qualifier("autoCancelSessionBookingJob") final JobDetail jobDetail,
            @Value("${jobs.autoCancelSessionBookingJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoWithdrawalUserCreditByWeeklyJob")
    public JobDetailFactoryBean autoWithdrawalUserCreditByWeeklyJob() {
        return createJobDetail(AutoWithdrawalCreditsByWeeklyJob.class);
    }

    @Bean(name = "autoWithdrawalUserCreditByWeeklyJobTrigger")
    public CronTriggerFactoryBean autoWithdrawalUserCreditByWeeklyJobTrigger(
            @Qualifier("autoWithdrawalUserCreditByWeeklyJob") final JobDetail jobDetail,
            @Value("${jobs.autoWithdrawalUserCreditByWeeklyJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "classBookingStartingReminderJob")
    public JobDetailFactoryBean remindClassStartingJob() {
        return createJobDetail(ClassBookingStartingReminderJob.class);
    }

    @Bean(name = "classBookingStartingReminderJobTrigger")
    public CronTriggerFactoryBean remindClassStartingJobTrigger(
            @Qualifier("classBookingStartingReminderJob") final JobDetail jobDetail,
            @Value("${jobs.classBookingStartingReminderJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }

    @Bean(name = "autoWithdrawalClubCreditByWeeklyJob")
    public JobDetailFactoryBean autoWithdrawalClubCreditByWeeklyJob() {
        return createJobDetail(AutoWithdrawalClubCreditByWeeklyJob.class);
    }

    @Bean(name = "autoWithdrawalClubCreditByWeeklyJobTrigger")
    public CronTriggerFactoryBean autoWithdrawalClubCreditByWeeklyJobTrigger(
            @Qualifier("autoWithdrawalClubCreditByWeeklyJob") final JobDetail jobDetail,
            @Value("${jobs.autoWithdrawalClubCreditByWeeklyJob.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }
    
    @Bean(name = "expiredUserCreditPackageNotificationJob")
    public JobDetailFactoryBean expiredUserCreditPackageNotificationJob() {
        return createJobDetail(ExpiredUserCreditPackageNotificationJob.class);
    }

    @Bean(name = "expiredUserCreditPackageNotificationJobTrigger")
    public CronTriggerFactoryBean expiredUserCreditPackageNotificationJobTrigger(
            @Qualifier("expiredUserCreditPackageNotificationJob") final JobDetail jobDetail,
            @Value("${jobs.expiredUserCreditPackageNotification.cronExpression}") final String cronExpression) {
        return createCronTrigger(jobDetail, cronExpression);
    }
}

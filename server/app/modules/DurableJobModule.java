package modules;

import akka.actor.ActorSystem;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import durablejobs.DurableJobName;
import durablejobs.DurableJobRegistry;
import durablejobs.DurableJobRunner;
import durablejobs.RecurringJobExecutionTimeResolvers;
import durablejobs.RecurringJobScheduler;
import durablejobs.jobs.OldJobCleanupJob;
import durablejobs.jobs.ReportingDashboardMonthlyRefreshJob;
import java.time.Duration;
import java.util.Random;
import repository.PersistedDurableJobRepository;
import repository.ReportingRepository;
import scala.concurrent.ExecutionContext;

/**
 * Configures {@link durablejobs.DurableJob}s with their {@link DurableJobName} and, if they are
 * recurring, their {@link durablejobs.RecurringJobExecutionTimeResolver}.
 */
public final class DurableJobModule extends AbstractModule {

  @Override
  protected void configure() {
    // Binding the scheduler class as an eager singleton runs the constructor
    // at server start time.
    bind(DurableJobRunnerScheduler.class).asEagerSingleton();
  }

  /** Schedules the job runner to run on an interval using the akka scheduling system. */
  public static final class DurableJobRunnerScheduler {

    @Inject
    public DurableJobRunnerScheduler(
        ActorSystem actorSystem,
        Config config,
        ExecutionContext executionContext,
        DurableJobRunner durableJobRunner,
        RecurringJobScheduler recurringJobScheduler) {
      int pollIntervalSeconds = config.getInt("durable_jobs.poll_interval_seconds");

      actorSystem
          .scheduler()
          .scheduleAtFixedRate(
              // Wait a random amount of time to decrease likelihood of synchronized
              // polling with another server.
              /* initialDelay= */ Duration.ofSeconds(new Random().nextInt(/* bound= */ 30)),
              /* interval= */ Duration.ofSeconds(pollIntervalSeconds),
              () -> {
                recurringJobScheduler.scheduleRecurringJobs();
                durableJobRunner.runJobs();
              },
              executionContext);
    }
  }

  @Provides
  public DurableJobRegistry provideDurableJobRegistry(
      PersistedDurableJobRepository persistedDurableJobRepository,
      ReportingRepository reportingRepository) {
    var durableJobRegistry = new DurableJobRegistry();

    durableJobRegistry.register(
        DurableJobName.OLD_JOB_CLEANUP,
        persistedDurableJob ->
            new OldJobCleanupJob(persistedDurableJobRepository, persistedDurableJob),
        new RecurringJobExecutionTimeResolvers.Sunday2Am());

    durableJobRegistry.register(
        DurableJobName.REPORTING_DASHBOARD_MONTHLY_REFRESH,
        persistedDurableJob ->
            new ReportingDashboardMonthlyRefreshJob(reportingRepository, persistedDurableJob),
        new RecurringJobExecutionTimeResolvers.FirstOfMonth2Am());

    return durableJobRegistry;
  }
}

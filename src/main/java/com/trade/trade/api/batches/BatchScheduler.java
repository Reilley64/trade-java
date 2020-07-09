package com.trade.trade.api.batches;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {
    private final JobLauncher jobLauncher;

    private final Job userSnapshotBatchJob;

    public BatchScheduler(JobLauncher jobLauncher, Job userSnapshotBatchJob) {
        this.jobLauncher = jobLauncher;
        this.userSnapshotBatchJob = userSnapshotBatchJob;
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void userSnapshotBatchSchedule() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(userSnapshotBatchJob, new JobParameters());
    }
}

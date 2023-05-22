package com.analytics.twitter.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.analytics.twitter.batch.JobCompletionNotificationListener;
import com.analytics.twitter.batch.steps.DataLoader;
import com.analytics.twitter.batch.steps.MonthStatsCollectorStep;
import com.analytics.twitter.batch.steps.ReverseMapping;
import com.analytics.twitter.repository.HashTagStatsRepository;
import com.analytics.twitter.repository.TweetRepository;
import com.analytics.twitter.repository.UserStatsRepository;

@Service
public class WorkflowService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ThreadPoolTaskExecutor exec;

    @Autowired
    private DataLoader dataLoader;
    @Autowired
    private JobCompletionNotificationListener listener;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserStatsRepository userStatsRepository;
    @Autowired
    private HashTagStatsRepository hashTagStatsRepository;

    @Autowired
    private MonthStatsCollectorStep monthStatsCollectorStep;

    public void startWorkflow(final String path) {
        try {
            JobParameters Parameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();
            Job job = buildJob(path);
            jobLauncher.run(job, Parameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    private Job buildJob(final String path) {
        var jobBuilder = new JobBuilder("Persist data", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(dataLoader.getStep(path)); // step 1
        for (int month = 1; month <= 12; month++) {
            var reverseMapper = new ReverseMapping(jobRepository, tweetRepository, transactionManager,
                    userStatsRepository, hashTagStatsRepository, exec, month);
            jobBuilder.next(reverseMapper.createStepForReverseMapping())
                    .next(monthStatsCollectorStep.createStep(month));
        }
        return jobBuilder.build();
    }
}

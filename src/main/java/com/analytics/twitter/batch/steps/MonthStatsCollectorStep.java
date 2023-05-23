package com.analytics.twitter.batch.steps;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.analytics.twitter.batch.steps.handlers.HomeScreenDataHandler;
import com.analytics.twitter.batch.steps.handlers.TweetsHashTagsHandler;
import com.analytics.twitter.batch.steps.handlers.UserStatsHandler;

@Service
public class MonthStatsCollectorStep {

    @Autowired
    private HomeScreenDataHandler homeScreenDataHandler;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TweetsHashTagsHandler tweetHashTagHandler;

    @Autowired
    private UserStatsHandler userStatsHandler;

    public Step createStep(int month) {
        return new StepBuilder("Monthly statistics for " + month, jobRepository)
                .tasklet((cc, cv) -> compute(month), transactionManager)
                .build();
    }

    private RepeatStatus compute(int month) {
        homeScreenDataHandler.handle(month);
        tweetHashTagHandler.handle(month);
        userStatsHandler.handle(month);
        return RepeatStatus.FINISHED;
    }

}

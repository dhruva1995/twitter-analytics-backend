package com.analytics.twitter.batch.steps;

import com.analytics.twitter.model.HashTagStats;
import com.analytics.twitter.model.Tweet;
import com.analytics.twitter.model.UserStats;
import com.analytics.twitter.repository.HashTagStatsRepository;
import com.analytics.twitter.repository.TweetRepository;
import com.analytics.twitter.repository.UserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReverseMapping implements StepExecutionListener {
    private static final Logger log = LoggerFactory.getLogger(ReverseMapping.class);
    private static final Pattern hashTagPattern = Pattern.compile("#\\w+");
    private static final Pattern mentionedUsersPattern = Pattern.compile("@\\w+");
    private final JobRepository jobRepository;
    private final TweetRepository tweetRepository;
    private final PlatformTransactionManager transactionManager;

    private final int month;


    private ReverseMappingState result = new ReverseMappingState();

    public static class ReverseMappingState {
        Map<String, UserStats> userStats;
        Map<String, HashTagStats> hashTagStas;

        ReverseMappingState() {
            this.userStats = new HashMap<>();
            this.hashTagStas = new HashMap<>();
        }

        public ReverseMappingState(Map<String, UserStats> userStats, Map<String, HashTagStats> hashTagStas) {
            this.userStats = userStats;
            this.hashTagStas = hashTagStas;
        }


    }

    private final UserStatsRepository userStatsRepository;

    private final HashTagStatsRepository hashTagStatsRepository;

    private final ThreadPoolTaskExecutor exec;

    public ReverseMapping(JobRepository jobRepository, TweetRepository tweetRepository,
                          PlatformTransactionManager transactionManager,
                          UserStatsRepository userStatsRepository, HashTagStatsRepository hashTagStatsRepository, ThreadPoolTaskExecutor exec,
                          int month) {
        this.jobRepository = jobRepository;
        this.tweetRepository = tweetRepository;
        this.transactionManager = transactionManager;

        this.exec = exec;
        this.month = month;
        this.hashTagStatsRepository = hashTagStatsRepository;
        this.userStatsRepository = userStatsRepository;
    }

    public Step createStepForReverseMapping() {

        TaskletStep step = new StepBuilder("Compute Reverse Mapping for " + month + " month", jobRepository)
                .<Tweet, ReverseMappingState>chunk(1000, transactionManager)
                .taskExecutor(this.exec)
                .reader(getReader())
                .processor(this::process)
                .listener(this)
                .writer(data -> this.write(data))
                .build();
        return step;
    }

    private ThreadPoolTaskExecutor getTaslExecutor() {
        var exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(10);
        return exec;
    }

    @Override
    public ExitStatus afterStep(StepExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to persist to the DB: ");
            userStatsRepository.saveAll(this.result.userStats.values());
            hashTagStatsRepository.saveAll(this.result.hashTagStas.values());
            this.result.userStats.clear();
            this.result.hashTagStas.clear();
            return ExitStatus.COMPLETED;
        }
        return ExitStatus.FAILED;
    }

    private synchronized void write(Chunk<? extends ReverseMappingState> states) {
        writeCore(states.getItems());
    }

    private void writeCore(List<? extends ReverseMappingState> states) {
        this.result = states.stream()
                .parallel()
                .map(obj -> (Object) obj)
                .map(obj -> (ReverseMappingState) obj)
                .reduce(this.result, (ReverseMappingState old, ReverseMappingState newState) -> this.merge(old, newState));
    }

    private ReverseMappingState merge(ReverseMappingState state1, ReverseMappingState state2) {
        return new ReverseMappingState(
                mergeUserStats(state1.userStats, state2.userStats),
                mergeHashTagStats(state1.hashTagStas, state2.hashTagStas)
        );


    }


    private Map<String, UserStats> mergeUserStats(Map<String, UserStats> map1, Map<String, UserStats> map2) {
        Map<String, UserStats> result = new HashMap<>();
        for (var entry : map1.entrySet()) {
            String key = entry.getKey();
            if (map2.containsKey(key)) {
                result.put(key, merge(entry.getValue(), map2.get(key)));
                map2.remove(key);
            } else {
                result.put(key, entry.getValue());
            }
        }
        result.putAll(map2);
        return result;
    }

    private Map<String, HashTagStats> mergeHashTagStats(Map<String, HashTagStats> map1,
                                                        Map<String, HashTagStats> map2) {
        Map<String, HashTagStats> result = new HashMap<>();
        for (var entry : map1.entrySet()) {
            String key = entry.getKey();
            if (map2.containsKey(key)) {
                result.put(key, merge(entry.getValue(), map2.get(key)));
                map2.remove(key);
            } else {
                result.put(key, entry.getValue());
            }
        }
        result.putAll(map2);
        return result;
    }

    private HashTagStats merge(HashTagStats v1, HashTagStats v2) {
        return HashTagStats.merge(v1, v2);
    }

    private UserStats merge(UserStats st1, UserStats st2) {
        return UserStats.merge(st1, st2);
    }


    public ReverseMappingState process(Tweet tweet) {
        System.out.println("Processing!");
        Map<String, HashTagStats> hashTagStas = new HashMap<>();
        Map<String, UserStats> userStats = new HashMap<>();


        int engagment = tweet.getLikeCount() + tweet.getRetweetCount();
        for (String hashTag : extractHashtags(tweet.getContent())) {
            hashTagStas.computeIfAbsent(hashTag, ht -> new HashTagStats(hashTag, month, 1))
                    .increaseEngagement(engagment);
        }

        for (var mentionedUser : extractTaggedUsers(tweet.getContent())) {
            userStats.computeIfAbsent(mentionedUser, un -> UserStats.with(mentionedUser, month)).increaseMentionedCount(1);
        }
        var userStat = userStats.computeIfAbsent(tweet.getUserName(), userName -> UserStats.with(userName, month));
        userStat.increaseTweetCount(1);
        userStat.increaseLikeCount(tweet.getLikeCount());
        userStat.increaseRetweetCount(tweet.getRetweetCount());

        userStat.increaseEngagementCount(engagment);
        return new ReverseMappingState(userStats, hashTagStas);
    }


    public static Set<String> extractHashtags(String tweetContent) {
        Set<String> hashtags = new HashSet<>();
        Matcher matcher = hashTagPattern.matcher(tweetContent);
        while (matcher.find()) {
            String hashtag = matcher.group().substring(1); // Exclude the '#' symbol
            hashtags.add(hashtag);
        }
        return hashtags;
    }

    public static Set<String> extractTaggedUsers(String tweetContent) {
        Set<String> taggedUsers = new HashSet<>();

        Matcher matcher = mentionedUsersPattern.matcher(tweetContent);

        while (matcher.find()) {
            String taggedUser = matcher.group().substring(1); // Exclude the '@' symbol
            taggedUsers.add(taggedUser);
        }

        return taggedUsers;
    }


    private ItemReader<? extends Tweet> getReader() {
        RepositoryItemReader<Tweet> reader = new RepositoryItemReader<>();
        reader.setRepository(tweetRepository);
        reader.setMethodName("findAllByMonth");
        reader.setArguments(Collections.singletonList(month));
        reader.setSort(new HashMap<>());
        return reader;
    }


}

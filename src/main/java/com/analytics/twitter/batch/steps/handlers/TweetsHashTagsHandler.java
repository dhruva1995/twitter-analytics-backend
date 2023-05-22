package com.analytics.twitter.batch.steps.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.analytics.twitter.model.TweetsHashTagStats;
import com.analytics.twitter.repository.HashTagStatsRepository;
import com.analytics.twitter.repository.TweetHashTagStatRepository;
import com.analytics.twitter.repository.TweetRepository;

@Service
public class TweetsHashTagsHandler {

    @Autowired
    private HashTagStatsRepository hashTagRepo;

    @Autowired
    private TweetRepository tweetRepo;

    @Autowired
    private TweetHashTagStatRepository targetRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void handle(int month) {
        TweetsHashTagStats pageData = new TweetsHashTagStats();
        pageData.setMonth(month);
        pageData.setTrendingHashTags(hashTagRepo.fetchTrendingHashTags(month));
        pageData.setMostLiked(tweetRepo.mostLikedInAMonth(month));
        pageData.setMostRetweeted(tweetRepo.mostRetweetedInAMonth(month));
        pageData.setMostEngaged(tweetRepo.mostEngagedInAMonth(month));
        System.out.println(pageData);

        mongoTemplate.remove(new Query(Criteria.where("month").is(month)), TweetsHashTagStats.class);

        targetRepo.save(pageData);

    }

}

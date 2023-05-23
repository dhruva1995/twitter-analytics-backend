package com.analytics.twitter.batch.steps.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.analytics.twitter.model.HashTagScorePair;
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

        List<HashTagScorePair> tagsWithScores = hashTagRepo.fetchTrendingHashTags(month)
                .stream()
                .map((List<Object> list) -> new HashTagScorePair(list.get(0).toString(), (int) list.get(1)))
                .toList();
        pageData.setTrendingHashTags(tagsWithScores);
        pageData.setMostLiked(tweetRepo.mostLikedInAMonth(month));
        pageData.setMostRetweeted(tweetRepo.mostRetweetedInAMonth(month));
        pageData.setMostEngaged(tweetRepo.mostEngagedInAMonth(month));

        mongoTemplate.remove(new Query(Criteria.where("month").is(month)), TweetsHashTagStats.class);

        targetRepo.save(pageData);

    }

}

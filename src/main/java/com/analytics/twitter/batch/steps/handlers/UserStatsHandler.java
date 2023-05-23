package com.analytics.twitter.batch.steps.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.analytics.twitter.model.UserStatPair;
import com.analytics.twitter.model.UsersMonthStats;
import com.analytics.twitter.repository.UserStatsMonthRepository;
import com.analytics.twitter.repository.UserStatsRepository;

@Service
public class UserStatsHandler {

    @Autowired
    private UserStatsMonthRepository targetRepo;

    @Autowired
    private UserStatsRepository userStatsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void handle(int month) {
        UsersMonthStats data = new UsersMonthStats();
        data.setMonth(month);
        data.setMostEngagedUsers(userStatsRepository.getMostEngagedUsers(month).stream()
                .map(l -> new UserStatPair(l.get(0).toString(), (int) l.get(1))).toList());
        data.setMostLikedUsers(userStatsRepository.getMostLikedUsers(month).stream()
                .map(l -> new UserStatPair(l.get(0).toString(), (int) l.get(1))).toList());
        data.setMostMentionedUsers(userStatsRepository.getMostTaggedUsers(month).stream()
                .map(l -> new UserStatPair(l.get(0).toString(), (int) l.get(1))).toList());
        data.setMostRetweetedUsers(userStatsRepository.getMostRetweetedUsers(month).stream()
                .map(l -> new UserStatPair(l.get(0).toString(), (int) l.get(1))).toList());
        mongoTemplate.remove(new Query(Criteria.where("month").is(month)), UsersMonthStats.class);

        targetRepo.save(data);
    }

}

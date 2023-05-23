package com.analytics.twitter.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analytics.twitter.model.HomeMonthStats;
import com.analytics.twitter.model.TweetsHashTagStats;
import com.analytics.twitter.model.UsersMonthStats;
import com.analytics.twitter.repository.HomeMonthStatRepository;
import com.analytics.twitter.repository.TweetHashTagStatRepository;
import com.analytics.twitter.repository.UserStatsMonthRepository;

@Service
public class APIDataService {

    @Autowired
    private HomeMonthStatRepository homePageRepo;

    @Autowired
    private TweetHashTagStatRepository trendingRepo;

    @Autowired
    private UserStatsMonthRepository usersRepo;

    public HomeMonthStats getHomePageForMonth(int month) {
        return homePageRepo.findByMonth(month).get(0);
    }

    public TweetsHashTagStats getTrendingPageData(int month) {
        return trendingRepo.findByMonth(month).get(0);
    }

    public UsersMonthStats getTrendingUsersData(int month) {
        return usersRepo.findByMonth(month).get(0);
    }

}

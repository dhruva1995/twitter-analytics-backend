package com.analytics.twitter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.analytics.twitter.model.UserStats;
import com.analytics.twitter.model.UserStatsCompositeKey;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, UserStatsCompositeKey> {

    @Query(value = "select user_name, like_count from user_stats where month_of_year = :month and like_count > 0order by like_count desc limit 10", nativeQuery = true)
    List<List<Object>> getMostLikedUsers(int month);

    @Query(value = "select user_name, retweet_count from user_stats where month_of_year = :month and retweet_count > 0 order by retweet_count desc limit 10", nativeQuery = true)
    List<List<Object>> getMostRetweetedUsers(int month);

    @Query(value = "select user_name, engagement_count from user_stats where month_of_year = :month and engagement_count > 0 order by engagement_count desc limit 10", nativeQuery = true)
    List<List<Object>> getMostEngagedUsers(int month);

    @Query(value = "select user_name, mentioned_count from user_stats where month_of_year = :month and mentioned_count > 0 order by mentioned_count desc limit 10", nativeQuery = true)
    List<List<Object>> getMostTaggedUsers(int month);

}

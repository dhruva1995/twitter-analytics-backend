package com.analytics.twitter.repository;

import com.analytics.twitter.model.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    @Query("SELECT COUNT(DISTINCT userName) from Tweet")
    Integer getDistinctUsers();


    @Query(value = "SELECT COUNT(id), COUNT(DISTINCT user_name), SUM(storage), `date` from tweet where month(`date`) = :month group by `date`", nativeQuery = true)
    List<List<Object>> getPerDayGraphsData(int month);

    @Query(value = "SELECT COUNT(id), Month(date) from tweet group by Month(date)", nativeQuery = true)
    List<List<Object>> getNoOfTweetsPerMonth();

    @Query(value = "SELECT * FROM tweet t WHERE MONTH(date) = :month", nativeQuery = true)
    Page<Tweet> findAllByMonth(int month, Pageable pageable);

    @Query(value = "SELECT month(date), COUNT(id), COUNT(DISTINCT user_name), SUM(like_count), SUM(retweet_count), SUM(storage) from tweet where month(date)=:month group by month(date)", nativeQuery = true)
    List<List<Object>> homeWidgetData(int month);
}

package com.analytics.twitter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.analytics.twitter.model.TweetsHashTagStats;

@Repository()
public interface TweetHashTagStatRepository extends MongoRepository<TweetsHashTagStats, ObjectId> {

    List<TweetsHashTagStats> findByMonth(int month);

}

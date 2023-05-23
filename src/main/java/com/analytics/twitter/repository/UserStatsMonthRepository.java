package com.analytics.twitter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.analytics.twitter.model.UsersMonthStats;

@Repository
public interface UserStatsMonthRepository extends MongoRepository<UsersMonthStats, ObjectId> {

    List<UsersMonthStats> findByMonth(int month);

}

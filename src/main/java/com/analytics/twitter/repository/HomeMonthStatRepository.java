package com.analytics.twitter.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.analytics.twitter.model.HomeMonthStats;

@Repository
public interface HomeMonthStatRepository extends MongoRepository<HomeMonthStats, ObjectId> {

    List<HomeMonthStats> findByMonth(int month);

}

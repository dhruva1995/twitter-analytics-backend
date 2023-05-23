package com.analytics.twitter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.analytics.twitter.model.HashTagCompositeKey;
import com.analytics.twitter.model.HashTagStats;

@Repository
public interface HashTagStatsRepository extends JpaRepository<HashTagStats, HashTagCompositeKey> {

    @Query(value = "select hash_tag, engagement_count from hash_tag_stats where month_of_year =:month order by engagement_count desc limit 30", nativeQuery = true)
    public List<List<Object>> fetchTrendingHashTags(int month);

}

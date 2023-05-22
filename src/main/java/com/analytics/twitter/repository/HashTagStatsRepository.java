package com.analytics.twitter.repository;

import com.analytics.twitter.model.HashTagCompositeKey;
import com.analytics.twitter.model.HashTagStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagStatsRepository extends JpaRepository<HashTagStats, HashTagCompositeKey> {
}

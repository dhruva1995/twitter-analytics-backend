package com.analytics.twitter.repository;

import com.analytics.twitter.model.UserStats;
import com.analytics.twitter.model.UserStatsCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, UserStatsCompositeKey> {
}

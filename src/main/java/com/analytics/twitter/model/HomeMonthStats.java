package com.analytics.twitter.model;

import static com.analytics.twitter.util.Converstion.getAs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document("home-month-stats")
public class HomeMonthStats {

    @Id
    private ObjectId id;

    private int month;

    private long tweetCount;

    private long uniqueUser;

    private long likeCount;

    private long retweetCount;

    private long storageCount;

    private long engagementCount;

    private Map<Long, Long> tweetsPerDay;

    private Map<Long, Long> activeUsersPerDay;

    private Map<Long, Long> storageUsagePerDay;

    public HomeMonthStats() {
        this.tweetsPerDay = new HashMap<>();
        this.activeUsersPerDay = new HashMap<>();
        this.storageUsagePerDay = new HashMap<>();
    }

    public static HomeMonthStats buildFromList(List<List<Object>> row) {
        if (row.isEmpty()) {
            return new HomeMonthStats();
        }

        HomeMonthStats data = new HomeMonthStats();
        data.month = getAs(row.get(0).get(0), Integer.class, 0);
        data.tweetCount = getAs(row.get(0).get(1), Long.class, 0l);
        data.uniqueUser = getAs(row.get(0).get(2), Long.class, 0l);
        data.likeCount = getAs(row.get(0).get(3), Long.class, 0l);
        data.retweetCount = getAs(row.get(0).get(4), Long.class, 0l);
        data.storageCount = getAs(row.get(0).get(5), Long.class, 0l);
        data.engagementCount = data.tweetCount + data.likeCount + data.retweetCount;
        return data;
    }

}

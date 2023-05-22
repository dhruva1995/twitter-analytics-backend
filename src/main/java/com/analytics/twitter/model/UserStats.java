package com.analytics.twitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity

@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserStatsCompositeKey.class)
public class UserStats {

    @Id
    private String userName;

    private int likeCount;

    private int retweetCount;

    private int engagementCount;

    private int tweetCount;

    private int mentionedCount;

    @Id
    private int monthOfYear;

    public void increaseLikeCount(int count) {
        this.likeCount += count;
    }

    public void increaseRetweetCount(int count) {
        this.retweetCount += count;
    }

    public void increaseEngagementCount(int count) {
        this.engagementCount += count;
    }

    public void increaseTweetCount(int count) {
        this.tweetCount += count;
    }

    public void increaseMentionedCount(int count) {
        this.mentionedCount += count;
    }

    public static UserStats with(String userName, int month) {
        UserStats stats = new UserStats();
        stats.monthOfYear = month;
        stats.setUserName(userName);
        return stats;
    }

    public static UserStats merge(UserStats st1, UserStats st2) {
        UserStats result = new UserStats();
        result.userName = st1.userName;
        result.monthOfYear = st1.monthOfYear;
        result.likeCount = st1.likeCount + st2.likeCount;
        result.retweetCount = st1.retweetCount + st2.retweetCount;
        result.engagementCount = st1.engagementCount + st2.engagementCount;
        result.tweetCount = st1.tweetCount + st2.tweetCount;
        result.mentionedCount = st1.mentionedCount + st2.mentionedCount;
        return result;
    }
}

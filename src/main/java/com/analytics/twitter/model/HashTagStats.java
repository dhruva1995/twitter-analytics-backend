package com.analytics.twitter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor

@IdClass(HashTagCompositeKey.class)
public class HashTagStats {

    @Id
    private String hashTag;

    @Id
    private int monthOfYear;

    private int engagementCount;

    public HashTagStats(String hashTag, int month, int engagementCount) {
        this.hashTag = hashTag;
        this.monthOfYear = month;
        this.engagementCount = engagementCount;
    }

    public void increaseEngagement(int amount) {
        this.engagementCount += amount;
    }

    public static HashTagStats merge(HashTagStats v1, HashTagStats v2) {
        return new HashTagStats(v1.hashTag, v1.monthOfYear, v1.engagementCount + v2.engagementCount);
    }

}

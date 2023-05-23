package com.analytics.twitter.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document("tweets-hashtag-stats")
public class TweetsHashTagStats {

    @Id
    private ObjectId id;

    private int month;

    @Field
    List<HashTagScorePair> trendingHashTags;

    @Field
    List<Tweet> mostLiked;

    @Field
    List<Tweet> mostRetweeted;

    @Field
    List<Tweet> mostEngaged;

    public TweetsHashTagStats() {
        this.trendingHashTags = new ArrayList<>();
        this.mostLiked = new ArrayList<>();
        this.mostRetweeted = new ArrayList<>();
        this.mostEngaged = new ArrayList<>();
    }

}

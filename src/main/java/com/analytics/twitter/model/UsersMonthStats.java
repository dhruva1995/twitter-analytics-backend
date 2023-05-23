package com.analytics.twitter.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Data;

@Document("user-stats")
@Data
public class UsersMonthStats {

    public UsersMonthStats() {
        this.mostLikedUsers = new ArrayList<>();
        this.mostRetweetedUsers = new ArrayList<>();
        this.mostEngagedUsers = new ArrayList<>();
        this.mostMentionedUsers = new ArrayList<>();
    }

    @Id
    private ObjectId id;

    private int month;

    @Field
    private List<UserStatPair> mostLikedUsers;

    @Field
    private List<UserStatPair> mostRetweetedUsers;

    @Field
    private List<UserStatPair> mostEngagedUsers;

    @Field
    private List<UserStatPair> mostMentionedUsers;

}

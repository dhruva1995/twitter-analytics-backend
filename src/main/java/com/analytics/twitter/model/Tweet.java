package com.analytics.twitter.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
@Table(indexes = @Index(columnList = "date"))
public class Tweet {
    // date,id,content,username,like_count,retweet_count
    @Id
    private Long id;

    private LocalDate date;

    @Column(length = 2048)
    private String content;

    private String userName;

    private int likeCount;

    private int retweetCount;

    private int storage;

    private int engagementCount;

}

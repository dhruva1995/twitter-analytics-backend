package com.analytics.twitter.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
@Table(indexes = @Index(columnList = "date"))
public class Tweet {
    //date,id,content,username,like_count,retweet_count
    @Id
    private Long id;

    private LocalDate date;

    @Column(length = 2048)
    private String content;

    private String userName;

    private int likeCount;

    private int retweetCount;

    private int storage;

}

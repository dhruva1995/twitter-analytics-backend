package com.analytics.twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Index | Column Name | Column Type |
 * -------------------------------------------
 * 0 | date | TEXT |
 * 1 | id | LONG |
 * 2 | content | TEXT |
 * 3 | username | TEXT |
 * 4 | like_count | INTEGER |
 * 5 | retweet_count | INTEGER |
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.analytics.twitter")
public class TwitterAnalyticsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterAnalyticsBackendApplication.class, args);

		// Table tweets = Table.read()
		// .csv("D:/interiew/tf-mini-project/twitter-analytics-backend/src/main/resources/input.csv");
	}

}

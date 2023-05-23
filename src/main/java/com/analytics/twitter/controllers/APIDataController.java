package com.analytics.twitter.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.analytics.twitter.model.HomeMonthStats;
import com.analytics.twitter.model.TweetsHashTagStats;
import com.analytics.twitter.model.UsersMonthStats;
import com.analytics.twitter.services.APIDataService;

@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.OPTIONS,
        RequestMethod.HEAD })
public class APIDataController {

    @Autowired
    private APIDataService service;

    @GetMapping("/home/{month}")
    public ResponseEntity<HomeMonthStats> getHomePageData(@PathVariable("month") final int month) {
        return new ResponseEntity<>(service.getHomePageForMonth(month), HttpStatus.OK);
    }

    @GetMapping("/trending/{month}")
    public ResponseEntity<TweetsHashTagStats> getTrendingHashTagsAndPostsForMonth(
            @PathVariable("month") final int month) {
        return new ResponseEntity<>(service.getTrendingPageData(month), HttpStatus.OK);
    }

    @GetMapping("/users/{month}")
    public ResponseEntity<UsersMonthStats> getTrendingUsersForMonth(
            @PathVariable("month") final int month) {
        return new ResponseEntity<>(service.getTrendingUsersData(month), HttpStatus.OK);
    }

}

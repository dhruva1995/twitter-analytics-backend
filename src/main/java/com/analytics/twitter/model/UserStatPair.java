package com.analytics.twitter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatPair {

    private String userName;

    private int value;

}

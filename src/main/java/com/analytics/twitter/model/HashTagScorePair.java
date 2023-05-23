package com.analytics.twitter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashTagScorePair {

    private String hashTag;

    private int engagement;

}

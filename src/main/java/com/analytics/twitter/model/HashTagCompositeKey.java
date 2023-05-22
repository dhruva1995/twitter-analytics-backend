package com.analytics.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class HashTagCompositeKey implements Serializable {
    private String hashTag;

    private int monthOfYear;
}

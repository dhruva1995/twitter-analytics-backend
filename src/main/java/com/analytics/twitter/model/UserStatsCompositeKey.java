package com.analytics.twitter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@Data
public class UserStatsCompositeKey implements Serializable {

    private String userName;

    private Integer monthOfYear;

}

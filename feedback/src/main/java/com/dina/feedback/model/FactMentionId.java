package com.dina.feedback.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
@Data
public class FactMentionId implements Serializable {
    private Long tweet;
    private Integer agency;

    public FactMentionId() {}

    public FactMentionId(Long tweet, Integer agency) {
        this.tweet = tweet;
        this.agency = agency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FactMentionId that)) return false;
        return Objects.equals(tweet, that.tweet) &&
                Objects.equals(agency, that.agency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweet, agency);
    }
}

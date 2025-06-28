package com.dina.feedback.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fact_mentions")
@IdClass(FactMentionId.class)
@Data
public class FactMention {

    @Id
    @ManyToOne
    @JoinColumn(name = "tweet_id")
    private FactFeedback tweet;

    @Id
    @ManyToOne
    @JoinColumn(name = "agency_key")
    private DimAgency agency;

    // Optionally: Add a timestamp, confidence score, etc. in future
}

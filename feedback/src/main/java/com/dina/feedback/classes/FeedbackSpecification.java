package com.dina.feedback.classes;

import com.dina.feedback.DTO.FeedbackFilter;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import com.dina.feedback.model.FactFeedback;
import com.dina.feedback.model.FactMention;
import com.dina.feedback.model.DimHashtag;



public class FeedbackSpecification {

    public static Specification<FactFeedback> build(FeedbackFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getFromDate() != null && filter.getToDate() != null) {
                Integer from = Integer.parseInt(filter.getFromDate().toString().replace("-", ""));
                Integer to = Integer.parseInt(filter.getToDate().toString().replace("-", ""));
                predicates.add(cb.between(root.get("date").get("dateKey"), from, to));
            }

            if (filter.getLanguage() != null) {
                predicates.add(cb.equal(root.get("language"), filter.getLanguage()));
            }

            if (filter.getUserId() != null) {
                predicates.add(cb.equal(root.get("user").get("userId"), filter.getUserId()));
            }

            if (filter.getLocation() != null) {
                predicates.add(cb.equal(root.get("location").get("locationString"), filter.getLocation()));
            }

            if (filter.getIssueCode() != null) {
                predicates.add(cb.equal(root.get("issue").get("issueClassCode"), filter.getIssueCode()));
            }

            if (filter.getAgencyMention() != null) {
                Join<FactFeedback, FactMention> mentions = root.join("mentions", JoinType.LEFT);
                predicates.add(cb.equal(mentions.get("agency").get("mention"), filter.getAgencyMention()));
            }

            if (filter.getHashtag() != null) {
                Join<FactFeedback, DimHashtag> hashtags = root.join("hashtags", JoinType.LEFT);
                predicates.add(cb.equal(hashtags.get("hashtag"), filter.getHashtag()));
            }

            query.distinct(true); // Avoid duplicates from joins
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

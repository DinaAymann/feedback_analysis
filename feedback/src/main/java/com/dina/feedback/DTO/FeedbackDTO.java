package com.dina.feedback.DTO;

import lombok.Data; // Required for @Data on all inner classes

import java.util.List;

@Data
public class FeedbackDTO {
    private String platform;
    private Long tweet_id;
    private String text;
    private String created_at;
    private String language;
    private List<String> hashtags;
    private List<String> mentions;
    private Metrics metrics;
    private IssueDTO issue;
    private UserDTO user;

    @Data
    public static class Metrics {
        private int retweet_count;
        private int reply_count;
        private int like_count;
        private int quote_count;
        private int bookmark_count;
        private int impression_count;
    }

    @Data
    public static class IssueDTO {
        private int issue_id;
        private IssueClass issue_class;

        @Data
        public static class IssueClass {
            private int issue_class_key;
            private String issue_class_code;
        }
    }

    @Data
    public static class UserDTO {
        private String user_id;
        private String username;
        private String created_at;
        private int followers_count;
        private int following_count;
        private int tweet_count;
        private int listed_count;
        private String location_string;
    }
}

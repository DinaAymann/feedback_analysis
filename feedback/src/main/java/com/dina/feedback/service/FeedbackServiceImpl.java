package com.dina.feedback.service;

import com.dina.feedback.DTO.FeedbackDTO;
import com.dina.feedback.model.*;
import com.dina.feedback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.dina.feedback.DTO.FeedbackFilter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import com.dina.feedback.classes.FeedbackSpecification;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FactFeedbackRepository feedbackRepo;
    private final DimUserRepository userRepo;
    private final DimIssueRepository issueRepo;
    private final DimDateRepository dateRepo;
    private final DimLocationRepository locationRepo;
    private final DimAgencyRepository agencyRepo;
    private final DimHashtagRepository hashtagRepo;
    private final FactMentionRepository mentionRepo;

    @Override
    public void insertFeedback(List<FeedbackDTO> dtos) {
        for (FeedbackDTO dto : dtos) {
            Long tweetId = dto.getTweet_id();
            if (feedbackRepo.existsById(tweetId)) continue; // skip duplicates
//
//            // 🧠 Process User
//            DimUser user = userRepo.findByUserId(dto.getUser().getUser_id())
//                    .orElseGet(() -> new DimUser());
//            user.setUserId(dto.getUser().getUser_id());
//            user.setUsername(dto.getUser().getUsername());
//            user.setAccountCreated(OffsetDateTime.parse(dto.getUser().getCreated_at()).toLocalDateTime());
//            user.setFollowersCount(dto.getUser().getFollowers_count());
//            user.setFollowingCount(dto.getUser().getFollowing_count());
//            user.setTweetCount(dto.getUser().getTweet_count());
//            user.setListedCount(dto.getUser().getListed_count());
//            user = userRepo.save(user);


            // 🧠 Process User
            DimUser user = userRepo.findByUserId(dto.getUser().getUser_id())
                    .orElseGet(() -> {
                        DimUser newUser = new DimUser();
                        newUser.setUserId(dto.getUser().getUser_id());
                        return newUser;
                    });

            boolean userChanged = false;

            if (!Objects.equals(user.getUsername(), dto.getUser().getUsername())) {
                user.setUsername(dto.getUser().getUsername());
                userChanged = true;
            }

            LocalDateTime createdAt = OffsetDateTime.parse(dto.getUser().getCreated_at()).toLocalDateTime();
            if (!Objects.equals(user.getAccountCreated(), createdAt)) {
                user.setAccountCreated(createdAt);
                userChanged = true;
            }

            if (!Objects.equals(user.getFollowersCount(), dto.getUser().getFollowers_count())) {
                user.setFollowersCount(dto.getUser().getFollowers_count());
                userChanged = true;
            }

            if (!Objects.equals(user.getFollowingCount(), dto.getUser().getFollowing_count())) {
                user.setFollowingCount(dto.getUser().getFollowing_count());
                userChanged = true;
            }

            if (!Objects.equals(user.getTweetCount(), dto.getUser().getTweet_count())) {
                user.setTweetCount(dto.getUser().getTweet_count());
                userChanged = true;
            }

            if (!Objects.equals(user.getListedCount(), dto.getUser().getListed_count())) {
                user.setListedCount(dto.getUser().getListed_count());
                userChanged = true;
            }

            if (user.getUserKey() == null || userChanged) {
                user = userRepo.save(user);
            }


            // 🧠 Process Location
            DimLocation location = locationRepo.findByLocationString(dto.getUser().getLocation_string())
                    .orElseGet(() -> {
                        DimLocation loc = new DimLocation();
                        loc.setLocationString(dto.getUser().getLocation_string());
                        return locationRepo.save(loc);
                    });

            // 🧠 Process Issue
            DimIssue issue = issueRepo.findByIssueIdAndIssueClassKey(
                    dto.getIssue().getIssue_id(), dto.getIssue().getIssue_class().getIssue_class_key()
            ).orElseGet(() -> {
                DimIssue i = new DimIssue();
                i.setIssueId(dto.getIssue().getIssue_id());
                i.setIssueClassKey(dto.getIssue().getIssue_class().getIssue_class_key());
                i.setIssueClassCode(dto.getIssue().getIssue_class().getIssue_class_code());
                return issueRepo.save(i);
            });

            // 🧠 Process Date
            LocalDate date = LocalDate.parse(dto.getCreated_at().substring(0, 10));
            Integer dateKey = Integer.parseInt(date.toString().replace("-", ""));
            DimDate dimDate = dateRepo.findById(dateKey).orElseGet(() -> {
                DimDate d = new DimDate();
                d.setDateKey(dateKey);
                d.setFullDate(date);
                d.setYear(date.getYear());
                d.setMonth(date.getMonthValue());
                d.setDay(date.getDayOfMonth());
                d.setWeek(date.get(WeekFields.ISO.weekOfYear()));
                d.setWeekdayName(date.getDayOfWeek().toString());
                return dateRepo.save(d);
            });

            // 🧠 Save Feedback
            FactFeedback feedback = new FactFeedback();
            feedback.setTweetId(tweetId);
            feedback.setPlatform(dto.getPlatform());
            feedback.setText(dto.getText());
            feedback.setLanguage(dto.getLanguage());
            feedback.setDate(dimDate);
            feedback.setUser(user);
            feedback.setIssue(issue);
            feedback.setLocation(location);
            feedback.setRetweetCount(dto.getMetrics().getRetweet_count());
            feedback.setReplyCount(dto.getMetrics().getReply_count());
            feedback.setLikeCount(dto.getMetrics().getLike_count());
            feedback.setQuoteCount(dto.getMetrics().getQuote_count());
            feedback.setBookmarkCount(dto.getMetrics().getBookmark_count());
            feedback.setImpressionCount(dto.getMetrics().getImpression_count());
            feedbackRepo.save(feedback);

            // 🧠 Mentions → FactMention
            for (String mentionStr : dto.getMentions()) {
                DimAgency agency = agencyRepo.findByMention(mentionStr)
                        .orElseGet(() -> {
                            DimAgency a = new DimAgency();
                            a.setMention(mentionStr);
                            return agencyRepo.save(a);
                        });

                FactMention mention = new FactMention();
                mention.setTweet(feedback);
                mention.setAgency(agency);
                mentionRepo.save(mention);
            }

            // 🧠 Hashtags
            for (String tag : dto.getHashtags()) {
                DimHashtag hashtag = new DimHashtag();
                hashtag.setTweet(feedback);
                hashtag.setHashtag(tag);
                hashtagRepo.save(hashtag);
            }
        }
    }

    @Override
    public List<FactFeedback> getAll() {
        return feedbackRepo.findAll();
    }

    @Override
    public List<FactFeedback> filterFeedbacks(FeedbackFilter filter) {
        return feedbackRepo.findAll(FeedbackSpecification.build(filter));
    }


    @Override
    public List<FactFeedback> filterByDateRange(LocalDate from, LocalDate to) {
        Integer fromKey = Integer.parseInt(from.toString().replace("-", ""));
        Integer toKey = Integer.parseInt(to.toString().replace("-", ""));
        return feedbackRepo.findByDate_DateKeyBetween(fromKey, toKey);
    }

    @Override
    public void updateUserTweetCount(String userId, int newTweetCount) {
        DimUser user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTweetCount(newTweetCount);
        userRepo.save(user);
    }
}

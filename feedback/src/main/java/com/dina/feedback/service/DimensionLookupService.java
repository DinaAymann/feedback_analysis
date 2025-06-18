package com.dina.feedback.service;

import com.dina.feedback.model.*;
import com.dina.feedback.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
public class DimensionLookupService {

    private final AgencyRepository agencyRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;

    // In-memory cache for frequently accessed dimensions
    private final ConcurrentHashMap<String, Long> agencyCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> locationCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> userCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> languageCache = new ConcurrentHashMap<>();

    public DimensionLookupService(AgencyRepository agencyRepository,
                                  LocationRepository locationRepository,
                                  UserRepository userRepository,
                                  LanguageRepository languageRepository) {
        this.agencyRepository = agencyRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;

        // Pre-load common languages
        initializeLanguages();
    }

    @Cacheable(value = "agencyKeys", key = "#agencyId")
    public Long getAgencyKey(Long agencyId) {
        String cacheKey = "AGENCY_" + agencyId;

        return agencyCache.computeIfAbsent(cacheKey, k -> {
            String agencyCode = "AGY_" + agencyId;

            return agencyRepository.findAgencyIdByCode(agencyCode)
                    .orElseGet(() -> {
                        // Create new agency dimension if doesn't exist
                        Agency newAgency = Agency.builder()
                                .agencyCode(agencyCode)
                                .agencyName("Agency " + agencyId)
                                .agencyType("GOVERNMENT")
                                .isActive(true)
                                .createdDate(LocalDateTime.now())
                                .build();

                        Agency saved = agencyRepository.save(newAgency);
                        log.info("Created new agency dimension: {}", agencyCode);
                        return saved.getAgencyId();
                    });
        });
    }

    @Cacheable(value = "locationKeys", key = "#locationId")
    public Long getLocationKey(Long locationId) {
        String cacheKey = "LOCATION_" + locationId;

        return locationCache.computeIfAbsent(cacheKey, k -> {
            String locationCode = "LOC_" + locationId;

            return locationRepository.findLocationIdByCode(locationCode)
                    .orElseGet(() -> {
                        // Create new location dimension if doesn't exist
                        Location newLocation = Location.builder()
                                .locationCode(locationCode)
                                .locationName("Location " + locationId)
                                .locationType("SERVICE_CENTER")
                                .countryCode("EG") // Default to Egypt
                                .isActive(true)
                                .createdDate(LocalDateTime.now())
                                .build();

                        Location saved = locationRepository.save(newLocation);
                        log.info("Created new location dimension: {}", locationCode);
                        return saved.getLocationId();
                    });
        });
    }

    @Cacheable(value = "userKeys", key = "#userId")
    public Long getUserKey(Long userId) {
        String cacheKey = "USER_" + userId;

        return userCache.computeIfAbsent(cacheKey, k -> {
            String userExternalId = "USR_" + userId;

            return userRepository.findUserIdByExternalId(userExternalId)
                    .orElseGet(() -> {
                        // Create new user dimension if doesn't exist
                        User newUser = User.builder()
                                .userExternalId(userExternalId)
                                .userSegment("CITIZEN")
                                .ageGroup("UNKNOWN")
                                .registrationDate(java.time.LocalDate.now())
                                .isActive(true)
                                .createdDate(LocalDateTime.now())
                                .build();

                        User saved = userRepository.save(newUser);
                        log.info("Created new user dimension: {}", userExternalId);
                        return saved.getUserId();
                    });
        });
    }

    @Cacheable(value = "languageKeys", key = "#languageCode")
    public Long getLanguageKey(String languageCode) {
        String normalizedCode = languageCode != null ? languageCode.toLowerCase() : "en";

        return languageCache.computeIfAbsent(normalizedCode, k -> {
            return languageRepository.findLanguageIdByCode(normalizedCode)
                    .orElseGet(() -> {
                        // Create new language dimension if doesn't exist
                        Language newLanguage = Language.builder()
                                .languageCode(normalizedCode)
                                .languageName(getLanguageName(normalizedCode))
                                .isActive(true)
                                .build();

                        Language saved = languageRepository.save(newLanguage);
                        log.info("Created new language dimension: {}", normalizedCode);
                        return saved.getLanguageId();
                    });
        });
    }

    private void initializeLanguages() {
        // Pre-populate common languages
        String[] commonLanguages = {"en", "ar", "fr", "de", "es"};
        String[] languageNames = {"English", "Arabic", "French", "German", "Spanish"};

        for (int i = 0; i < commonLanguages.length; i++) {
            String code = commonLanguages[i];
            String name = languageNames[i];

            if (languageRepository.findByLanguageCodeAndIsActive(code, true).isEmpty()) {
                Language language = Language.builder()
                        .languageCode(code)
                        .languageName(name)
                        .isActive(true)
                        .build();

                languageRepository.save(language);
                log.info("Initialized language: {} - {}", code, name);
            }
        }
    }

    private String getLanguageName(String languageCode) {
        return switch (languageCode.toLowerCase()) {
            case "en" -> "English";
            case "ar" -> "Arabic";
            case "fr" -> "French";
            case "de" -> "German";
            case "es" -> "Spanish";
            case "it" -> "Italian";
            case "pt" -> "Portuguese";
            case "ru" -> "Russian";
            case "zh" -> "Chinese";
            case "ja" -> "Japanese";
            default -> "Unknown Language";
        };
    }

    // Clear cache methods for testing/maintenance
    public void clearCaches() {
        agencyCache.clear();
        locationCache.clear();
        userCache.clear();
        languageCache.clear();
        log.info("All dimension caches cleared");
    }
}
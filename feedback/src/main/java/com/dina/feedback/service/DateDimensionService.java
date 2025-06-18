package com.dina.feedback.service;

import com.dina.feedback.model.DimDate;
import com.dina.feedback.repository.DateRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class DateDimensionService {

    private final DateRepository dateRepository;

    public DateDimensionService(DateRepository dateRepository) {
        this.dateRepository = dateRepository;
    }

    @Bean
    public ApplicationRunner initializeDateDimension() {
        return args -> populateDateDimension();
    }

    private void populateDateDimension() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int dateKey = currentDate.getYear() * 10000 +
                    currentDate.getMonthValue() * 100 +
                    currentDate.getDayOfMonth();

            if (!dateRepository.existsByDateKey(dateKey)) {
                DimDate dimDate = DimDate.builder()
                        .dateKey(dateKey)
                        .fullDate(currentDate)
                        .year(currentDate.getYear())
                        .quarter((currentDate.getMonthValue() - 1) / 3 + 1)
                        .month(currentDate.getMonthValue())
                        .week(currentDate.getDayOfYear() / 7 + 1)
                        .dayOfYear(currentDate.getDayOfYear())
                        .dayOfMonth(currentDate.getDayOfMonth())
                        .dayOfWeek(currentDate.getDayOfWeek().getValue())
                        .monthName(currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                        .dayName(currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                        .isWeekend(currentDate.getDayOfWeek().getValue() >= 6)
                        .isHoliday(false) // Implement holiday logic as needed
                        .build();

                dateRepository.save(dimDate);
            }

            currentDate = currentDate.plusDays(1);
        }
    }
}
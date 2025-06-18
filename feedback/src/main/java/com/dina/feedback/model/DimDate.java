package com.dina.feedback.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "dim_date")
@Data
@AllArgsConstructor
@Builder
public class DimDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_key", unique = true, nullable = false)
    private String dateKey; // Format: YYYYMMDD

    @Column(name = "full_date", nullable = false)
    private LocalDate fullDate;

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 1-7 (Monday-Sunday)

    @Column(name = "day_name")
    private String dayName; // Monday, Tuesday, etc.

    @Column(name = "day_of_month")
    private Integer dayOfMonth; // 1-31

    @Column(name = "day_of_year")
    private Integer dayOfYear; // 1-366

    @Column(name = "week_of_year")
    private Integer weekOfYear; // 1-53

    @Column(name = "month_number")
    private Integer monthNumber; // 1-12

    @Column(name = "month_name")
    private String monthName; // January, February, etc.

    @Column(name = "quarter")
    private Integer quarter; // 1-4

    @Column(name = "year")
    private Integer year;

    @Column(name = "is_weekend")
    private Boolean isWeekend;

    @Column(name = "is_holiday")
    private Boolean isHoliday;

    // Constructors
    public DimDate() {}

    public DimDate(LocalDate date) {
        this.fullDate = date;
        this.dateKey = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.dayOfWeek = date.getDayOfWeek().getValue();
        this.dayName = date.getDayOfWeek().name();
        this.dayOfMonth = date.getDayOfMonth();
        this.dayOfYear = date.getDayOfYear();
        this.monthNumber = date.getMonthValue();
        this.monthName = date.getMonth().name();
        this.quarter = (date.getMonthValue() - 1) / 3 + 1;
        this.year = date.getYear();
        this.isWeekend = (dayOfWeek == 6 || dayOfWeek == 7); // Saturday or Sunday
        this.isHoliday = false; // Default to false, can be set manually

        // Calculate week of year
        this.weekOfYear = date.getDayOfYear() / 7 + 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public LocalDate getFullDate() {
        return fullDate;
    }

    public void setFullDate(LocalDate fullDate) {
        this.fullDate = fullDate;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(Integer dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public Integer getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(Integer weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public Integer getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(Integer monthNumber) {
        this.monthNumber = monthNumber;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    public Boolean getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }

    @Override
    public String toString() {
        return "DimDate{" +
                "id=" + id +
                ", dateKey='" + dateKey + '\'' +
                ", fullDate=" + fullDate +
                ", dayName='" + dayName + '\'' +
                ", monthName='" + monthName + '\'' +
                ", year=" + year +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimDate)) return false;
        DimDate dimDate = (DimDate) o;
        return dateKey != null ? dateKey.equals(dimDate.dateKey) : dimDate.dateKey == null;
    }

    @Override
    public int hashCode() {
        return dateKey != null ? dateKey.hashCode() : 0;
    }
}
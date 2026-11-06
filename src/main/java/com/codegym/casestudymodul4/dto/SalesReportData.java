package com.codegym.casestudymodul4.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesReportData(
    String period,
    String periodLabel,
    LocalDate selectedDay,
    String selectedMonth,
    Integer selectedQuarter,
    Integer selectedYear,
    LocalDate startDate,
    LocalDate endDate,
    long orderCount,
    BigDecimal revenue,
    BigDecimal averageOrderValue,
    List<SalesReportBucket> buckets) {}

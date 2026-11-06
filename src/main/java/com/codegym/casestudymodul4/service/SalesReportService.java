package com.codegym.casestudymodul4.service;

import com.codegym.casestudymodul4.dto.SalesReportBucket;
import com.codegym.casestudymodul4.dto.SalesReportData;
import com.codegym.casestudymodul4.repository.SalesOrderReportRow;
import com.codegym.casestudymodul4.repository.SalesOrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalesReportService {
  private static final BigDecimal ZERO = BigDecimal.ZERO;
  private static final String CANCELLED_STATUS = "Đã hủy";

  private final SalesOrderRepository orders;

  public SalesReportService(SalesOrderRepository orders) {
    this.orders = orders;
  }

  public SalesReportData generate(
      String requestedPeriod,
      LocalDate requestedDay,
      String requestedMonth,
      Integer requestedQuarter,
      Integer requestedYear) {
    String period = normalizePeriod(requestedPeriod);
    LocalDate today = LocalDate.now();
    int year = requestedYear == null ? today.getYear() : requestedYear;
    LocalDate selectedDay = requestedDay == null ? today : requestedDay;
    YearMonth selectedMonth = parseMonth(requestedMonth, today);
    int quarter = validQuarter(requestedQuarter) ? requestedQuarter : (today.getMonthValue() - 1) / 3 + 1;

    LocalDate startDate;
    LocalDate endExclusive;
    List<String> labels = new ArrayList<>();

    switch (period) {
      case "day" -> {
        startDate = selectedDay;
        endExclusive = startDate.plusDays(1);
        for (int hour = 0; hour < 24; hour++) {
          labels.add(String.format("%02d:00", hour));
        }
      }
      case "quarter" -> {
        int firstMonth = (quarter - 1) * 3 + 1;
        startDate = LocalDate.of(year, firstMonth, 1);
        endExclusive = startDate.plusMonths(3);
        for (int month = firstMonth; month < firstMonth + 3; month++) {
          labels.add("Tháng " + month);
        }
      }
      case "year" -> {
        startDate = LocalDate.of(year, 1, 1);
        endExclusive = startDate.plusYears(1);
        for (int month = 1; month <= 12; month++) {
          labels.add("Tháng " + month);
        }
      }
      default -> {
        startDate = selectedMonth.atDay(1);
        endExclusive = selectedMonth.plusMonths(1).atDay(1);
        for (int day = 1; day <= selectedMonth.lengthOfMonth(); day++) {
          labels.add(String.format("%02d/%02d", day, selectedMonth.getMonthValue()));
        }
      }
    }

    List<MutableBucket> buckets = new ArrayList<>();
    labels.forEach(label -> buckets.add(new MutableBucket(label)));
    BigDecimal revenue = ZERO;
    long orderCount = 0;
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endExclusive.atStartOfDay();

    for (SalesOrderReportRow order : orders.findReportRows(start, end)) {
      if (CANCELLED_STATUS.equalsIgnoreCase(order.getStatus())) {
        continue;
      }

      int bucketIndex = bucketIndex(period, order.getCreatedAt(), startDate);
      if (bucketIndex < 0 || bucketIndex >= buckets.size()) {
        continue;
      }

      BigDecimal orderTotal = order.getTotalAmount() == null ? ZERO : order.getTotalAmount();
      MutableBucket bucket = buckets.get(bucketIndex);
      bucket.orderCount++;
      bucket.revenue = bucket.revenue.add(orderTotal);
      orderCount++;
      revenue = revenue.add(orderTotal);
    }

    BigDecimal maxBucketRevenue = buckets.stream()
        .map(bucket -> bucket.revenue)
        .max(BigDecimal::compareTo)
        .orElse(ZERO);
    List<SalesReportBucket> reportBuckets = buckets.stream()
        .map(bucket -> toReportBucket(bucket, maxBucketRevenue))
        .toList();
    BigDecimal averageOrderValue = orderCount == 0
        ? ZERO
        : revenue.divide(BigDecimal.valueOf(orderCount), 0, RoundingMode.HALF_UP);

    return new SalesReportData(
        period,
        periodLabel(period),
        selectedDay,
        selectedMonth.toString(),
        quarter,
        year,
        startDate,
        endExclusive.minusDays(1),
        orderCount,
        revenue,
        averageOrderValue,
        reportBuckets);
  }

  private String normalizePeriod(String period) {
    return switch (period == null ? "" : period.toLowerCase()) {
      case "day", "month", "quarter", "year" -> period.toLowerCase();
      default -> "month";
    };
  }

  private YearMonth parseMonth(String value, LocalDate today) {
    if (value == null || value.isBlank()) {
      return YearMonth.from(today);
    }
    try {
      return YearMonth.parse(value);
    } catch (DateTimeParseException exception) {
      return YearMonth.from(today);
    }
  }

  private boolean validQuarter(Integer quarter) {
    return quarter != null && quarter >= 1 && quarter <= 4;
  }

  private int bucketIndex(String period, LocalDateTime createdAt, LocalDate startDate) {
    return switch (period) {
      case "day" -> createdAt.getHour();
      case "quarter" -> createdAt.getMonthValue() - startDate.getMonthValue();
      case "year" -> createdAt.getMonthValue() - 1;
      default -> createdAt.getDayOfMonth() - 1;
    };
  }

  private SalesReportBucket toReportBucket(MutableBucket bucket, BigDecimal maxRevenue) {
    int width = 0;
    if (maxRevenue.compareTo(ZERO) > 0 && bucket.revenue.compareTo(ZERO) > 0) {
      width = Math.max(3, bucket.revenue.multiply(BigDecimal.valueOf(100))
          .divide(maxRevenue, 0, RoundingMode.HALF_UP).intValue());
    }
    return new SalesReportBucket(bucket.label, bucket.orderCount, bucket.revenue, width);
  }

  private String periodLabel(String period) {
    return switch (period) {
      case "day" -> "Theo ngày";
      case "quarter" -> "Theo quý";
      case "year" -> "Theo năm";
      default -> "Theo tháng";
    };
  }

  private static class MutableBucket {
    private final String label;
    private long orderCount;
    private BigDecimal revenue = ZERO;

    private MutableBucket(String label) {
      this.label = label;
    }

  }
}

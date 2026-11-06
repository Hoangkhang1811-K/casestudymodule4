package com.codegym.casestudymodul4.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SalesOrderReportRow {
  LocalDateTime getCreatedAt();

  BigDecimal getTotalAmount();

  String getStatus();
}

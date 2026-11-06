package com.codegym.casestudymodul4.dto;

import java.math.BigDecimal;

public record SalesReportBucket(
    String label, long orderCount, BigDecimal revenue, int barWidthPercent) {}

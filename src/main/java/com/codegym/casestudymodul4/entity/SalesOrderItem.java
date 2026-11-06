package com.codegym.casestudymodul4.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class SalesOrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private SalesOrder order;

  private Long productId;
  private String productNameSnapshot;
  private BigDecimal unitPrice;
  private Integer quantity;
  private BigDecimal lineTotal;

  public SalesOrderItem() {}

  public SalesOrderItem(Long productId, String name, BigDecimal price, Integer quantity) {
    this.productId = productId;
    this.productNameSnapshot = name;
    this.unitPrice = price;
    this.quantity = quantity;
    this.lineTotal = price.multiply(BigDecimal.valueOf(quantity));
  }

  public void setOrder(SalesOrder order) {
    this.order = order;
  }

  public String getProductNameSnapshot() {
    return productNameSnapshot;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getLineTotal() {
    return lineTotal;
  }
}

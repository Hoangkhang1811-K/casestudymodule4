package com.codegym.casestudymodul4.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_order")
public class SalesOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String orderCode;

  private String username;
  private String customerName;
  private String phone;
  private String address;
  private String status = "Đang xử lý";
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SalesOrderItem> items = new ArrayList<>();

  public SalesOrder() {}

  public SalesOrder(
      String orderCode, String username, String customerName, String phone, String address) {
    this.orderCode = orderCode;
    this.username = username;
    this.customerName = customerName;
    this.phone = phone;
    this.address = address;
  }

  public void addItem(SalesOrderItem item) {
    items.add(item);
    item.setOrder(this);
  }

  public Long getId() {
    return id;
  }

  public String getOrderCode() {
    return orderCode;
  }

  public String getUsername() {
    return username;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }

  public String getStatus() {
    return status;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public BigDecimal getSubtotalAmount() {
    return items.stream()
        .map(SalesOrderItem::getLineTotal)
        .filter(itemTotal -> itemTotal != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getShippingFee() {
    return totalAmount.subtract(getSubtotalAmount()).max(BigDecimal.ZERO);
  }

  public BigDecimal getTaxAmount() {
    return BigDecimal.ZERO;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<SalesOrderItem> getItems() {
    return items;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

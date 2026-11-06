package com.codegym.casestudymodul4.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 120)
  private String name;

  @Column(length = 1200)
  private String description;

  @NotNull
  @DecimalMin("0.0")
  private BigDecimal price;

  @NotNull
  @Min(0)
  private Integer stock;

  private String imageUrl;
  private String badge;
  private String color = "#f4ede5";
  private boolean active = true;

  @ManyToOne(fetch = FetchType.EAGER)
  private Category category;

  public Product() {}

  public Product(
      String name,
      String description,
      BigDecimal price,
      int stock,
      String imageUrl,
      String badge,
      String color,
      Category category) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.imageUrl = imageUrl;
    this.badge = badge;
    this.color = color;
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getStock() {
    return stock;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getBadge() {
    return badge;
  }

  public String getColor() {
    return color;
  }

  public boolean isActive() {
    return active;
  }

  public Category getCategory() {
    return category;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setBadge(String badge) {
    this.badge = badge;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
}

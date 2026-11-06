package com.codegym.casestudymodul4.entity;

import jakarta.persistence.*;

@Entity
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  private String icon = "✦";

  public Category() {}

  public Category(String name, String icon) {
    this.name = name;
    this.icon = icon;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}

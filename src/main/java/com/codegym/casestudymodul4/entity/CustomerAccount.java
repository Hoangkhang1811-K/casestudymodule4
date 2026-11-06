package com.codegym.casestudymodul4.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "customer_account")
public class CustomerAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  @Pattern(regexp = "^[a-zA-Z0-9_]{3,30}$")
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false, unique = true)
  @Email
  private String email;

  private String fullName;
  private String role = "USER";
  private boolean enabled = true;

  public CustomerAccount() {}

  public CustomerAccount(
      String username, String passwordHash, String email, String fullName, String role) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.email = email;
    this.fullName = fullName;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getEmail() {
    return email;
  }

  public String getFullName() {
    return fullName;
  }

  public String getRole() {
    return role;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}

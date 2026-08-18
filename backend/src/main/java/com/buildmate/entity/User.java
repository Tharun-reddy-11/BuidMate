package com.buildmate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false) private String fullName;
  @Column(nullable = false, unique = true) private String email;
  @Column(nullable = false) private String phone;
  @JsonIgnore @Column(nullable = false) private String passwordHash;
  @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role = Role.CUSTOMER;
  @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
  public enum Role { CUSTOMER, ADMIN }
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
  public Role getRole(){return role;} public void setRole(Role v){role=v;}
  public Instant getCreatedAt(){return createdAt;}
}


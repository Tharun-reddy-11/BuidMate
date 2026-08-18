package com.buildmate.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="project_requests")
public class ProjectRequest {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String requestCode;
  @JsonIgnore @ManyToOne private User user;
  @ManyToOne private ProjectTemplate template;
  @Column(nullable=false) private String projectTitle;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private ProjectType projectType;
  @Column(nullable=false) private String domain;
  @Column(nullable=false, length=3000) private String description;
  @Column(nullable=false) private String teamLeadName;
  @Column(nullable=false) private String phone;
  @Column(nullable=false) private String email;
  private String collegeName;
  private String rollNumber;
  private String section;
  private String department;
  private Integer teamSize;
  private String preferredStack;
  private String budget;
  private LocalDate expectedDeliveryDate;
  @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status = Status.NEW;
  @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
  public enum ProjectType { COLLEGE_PROJECT, RESUME_PROJECT, CUSTOM_PROJECT }
  public enum Status { NEW, ACCEPTED, REJECTED, CONTACTED, DISCUSSION, CONFIRMED, DEVELOPING, IN_PROGRESS, COMPLETED, CANCELLED }
  public Long getId(){return id;} public String getRequestCode(){return requestCode;} public void setRequestCode(String v){requestCode=v;}
  public User getUser(){return user;} public void setUser(User v){user=v;}
  public ProjectTemplate getTemplate(){return template;} public void setTemplate(ProjectTemplate v){template=v;}
  public String getProjectTitle(){return projectTitle;} public void setProjectTitle(String v){projectTitle=v;} public ProjectType getProjectType(){return projectType;} public void setProjectType(ProjectType v){projectType=v;}
  public String getDomain(){return domain;} public void setDomain(String v){domain=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getTeamLeadName(){return teamLeadName;} public void setTeamLeadName(String v){teamLeadName=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getCollegeName(){return collegeName;} public void setCollegeName(String v){collegeName=v;}
  public String getRollNumber(){return rollNumber;} public void setRollNumber(String v){rollNumber=v;} public String getSection(){return section;} public void setSection(String v){section=v;} public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
  public Integer getTeamSize(){return teamSize;} public void setTeamSize(Integer v){teamSize=v;} public String getPreferredStack(){return preferredStack;} public void setPreferredStack(String v){preferredStack=v;}
  public String getBudget(){return budget;} public void setBudget(String v){budget=v;} public LocalDate getExpectedDeliveryDate(){return expectedDeliveryDate;} public void setExpectedDeliveryDate(LocalDate v){expectedDeliveryDate=v;}
  public Status getStatus(){return status;} public void setStatus(Status v){status=v;} public Instant getCreatedAt(){return createdAt;}
}

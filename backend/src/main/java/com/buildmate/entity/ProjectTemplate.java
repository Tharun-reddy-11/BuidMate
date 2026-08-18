package com.buildmate.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project_templates")
public class ProjectTemplate {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String slug;
  @Column(nullable=false) private String title;
  @Column(nullable=false) private String category;
  @Column(nullable=false, length=2000) private String description;
  @Column(nullable=false) private String technology;
  @Column(nullable=false) private BigDecimal collegePrice;
  @Column(nullable=false) private BigDecimal resumePrice;
  @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="template_features", joinColumns=@JoinColumn(name="template_id"))
  @Column(name="feature") private List<String> features = new ArrayList<>();
  @Column(nullable=false) private boolean featured;
  @Column(nullable=false) private boolean published = true;
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
  public String getTitle(){return title;} public void setTitle(String v){title=v;}
  public String getCategory(){return category;} public void setCategory(String v){category=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getTechnology(){return technology;} public void setTechnology(String v){technology=v;}
  public BigDecimal getCollegePrice(){return collegePrice;} public void setCollegePrice(BigDecimal v){collegePrice=v;}
  public BigDecimal getResumePrice(){return resumePrice;} public void setResumePrice(BigDecimal v){resumePrice=v;}
  public List<String> getFeatures(){return features;} public void setFeatures(List<String> v){features=v;}
  public boolean isFeatured(){return featured;} public void setFeatured(boolean v){featured=v;}
  public boolean isPublished(){return published;} public void setPublished(boolean v){published=v;}
}


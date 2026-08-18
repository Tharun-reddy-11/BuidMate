package com.buildmate.repository;
import com.buildmate.entity.ProjectTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProjectTemplateRepository extends JpaRepository<ProjectTemplate,Long>{List<ProjectTemplate> findByPublishedTrueOrderByFeaturedDescTitleAsc(); Optional<ProjectTemplate> findBySlugAndPublishedTrue(String slug);}


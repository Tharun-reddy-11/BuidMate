package com.buildmate.repository;
import com.buildmate.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest,Long>{List<ProjectRequest> findAllByOrderByCreatedAtDesc(); long countByStatus(ProjectRequest.Status status);}

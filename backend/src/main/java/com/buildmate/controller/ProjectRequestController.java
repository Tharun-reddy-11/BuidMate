package com.buildmate.controller;
import com.buildmate.dto.ApiDtos.RequestCreate;
import com.buildmate.entity.ProjectRequest;
import com.buildmate.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.Year;
import java.util.*;
@RestController @RequestMapping("/api/requests")
public class ProjectRequestController {private final ProjectRequestRepository requests;private final ProjectTemplateRepository templates;public ProjectRequestController(ProjectRequestRepository r,ProjectTemplateRepository t){requests=r;templates=t;}
  @PostMapping public ResponseEntity<?> create(@Valid @RequestBody RequestCreate dto){var p=new ProjectRequest();p.setRequestCode("PRJ-"+Year.now().getValue()+"-"+UUID.randomUUID().toString().substring(0,6).toUpperCase());if(dto.templateId()!=null)p.setTemplate(templates.findById(dto.templateId()).orElse(null));p.setProjectTitle(dto.projectTitle());p.setProjectType(dto.projectType());p.setDomain(dto.domain());p.setDescription(dto.description());p.setTeamLeadName(dto.teamLeadName());p.setPhone(dto.phone());p.setEmail(dto.email());p.setCollegeName(dto.collegeName());p.setRollNumber(dto.rollNumber());p.setSection(dto.section());p.setDepartment(dto.department());p.setTeamSize(dto.teamSize());p.setPreferredStack(dto.preferredStack());p.setBudget(dto.budget());p.setExpectedDeliveryDate(dto.expectedDeliveryDate());return ResponseEntity.status(201).body(requests.save(p));}
}

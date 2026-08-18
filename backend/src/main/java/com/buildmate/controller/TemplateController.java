package com.buildmate.controller;
import com.buildmate.entity.ProjectTemplate;
import com.buildmate.repository.ProjectTemplateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/templates")
public class TemplateController {private final ProjectTemplateRepository repo;public TemplateController(ProjectTemplateRepository r){repo=r;}@GetMapping public List<ProjectTemplate> all(){return repo.findByPublishedTrueOrderByFeaturedDescTitleAsc();}@GetMapping("/{slug}")public ResponseEntity<ProjectTemplate> one(@PathVariable String slug){return repo.findBySlugAndPublishedTrue(slug).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}}


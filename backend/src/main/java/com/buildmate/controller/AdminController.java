package com.buildmate.controller;

import com.buildmate.dto.ApiDtos.DashboardStats;
import com.buildmate.dto.ApiDtos.StatusUpdate;
import com.buildmate.entity.ProjectRequest;
import com.buildmate.repository.ProjectRequestRepository;
import com.buildmate.repository.UserRepository;
import com.buildmate.service.ProjectMailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final ProjectRequestRepository requests;
  private final UserRepository users;
  private final ProjectMailService mail;

  public AdminController(ProjectRequestRepository requests, UserRepository users, ProjectMailService mail) {
    this.requests=requests;
    this.users=users;
    this.mail=mail;
  }

  @GetMapping("/requests")
  public List<ProjectRequest> requests() {
    return requests.findAllByOrderByCreatedAtDesc();
  }

  @GetMapping("/stats")
  public DashboardStats stats() {
    long total=requests.count();
    long done=requests.countByStatus(ProjectRequest.Status.COMPLETED);
    long fresh=requests.countByStatus(ProjectRequest.Status.NEW);
    long active=total-fresh-done-requests.countByStatus(ProjectRequest.Status.CANCELLED)-requests.countByStatus(ProjectRequest.Status.REJECTED);
    return new DashboardStats(total,fresh,active,done,Math.max(0,users.count()-1));
  }

  @Transactional
  @PatchMapping("/requests/{id}/status")
  public ResponseEntity<?> status(@PathVariable Long id, @Valid @RequestBody StatusUpdate update) {
    ProjectRequest request=requests.findById(id).orElse(null);
    if (request==null) return ResponseEntity.notFound().build();

    boolean accepting=update.status()==ProjectRequest.Status.ACCEPTED
        && request.getStatus()!=ProjectRequest.Status.ACCEPTED;
    request.setStatus(update.status());
    ProjectRequest saved=requests.saveAndFlush(request);
    if (accepting) {
      var receipt=mail.sendAccepted(saved);
      saved.recordAcceptanceEmail(receipt.messageId());
      saved=requests.save(saved);
    }
    return ResponseEntity.ok(saved);
  }

  @Transactional
  @PostMapping("/requests/{id}/acceptance-email")
  public ResponseEntity<?> resendAcceptanceEmail(@PathVariable Long id) {
    ProjectRequest request=requests.findById(id).orElse(null);
    if (request==null) return ResponseEntity.notFound().build();
    if (request.getStatus()!=ProjectRequest.Status.ACCEPTED) {
      return ResponseEntity.badRequest().body(Map.of("message","Accept the request before sending its acceptance email."));
    }
    var receipt=mail.sendAccepted(request);
    request.recordAcceptanceEmail(receipt.messageId());
    return ResponseEntity.ok(requests.save(request));
  }
}

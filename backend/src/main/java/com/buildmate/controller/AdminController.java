package com.buildmate.controller;
import com.buildmate.dto.ApiDtos.*;
import com.buildmate.entity.ProjectRequest;
import com.buildmate.repository.*;
import com.buildmate.service.ProjectMailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@RestController @RequestMapping("/api/admin")
public class AdminController {private final ProjectRequestRepository requests;private final UserRepository users;private final ProjectMailService mail;public AdminController(ProjectRequestRepository r,UserRepository u,ProjectMailService m){requests=r;users=u;mail=m;}@GetMapping("/requests")public List<ProjectRequest> requests(){return requests.findAllByOrderByCreatedAtDesc();}@GetMapping("/stats")public DashboardStats stats(){long total=requests.count(),done=requests.countByStatus(ProjectRequest.Status.COMPLETED),fresh=requests.countByStatus(ProjectRequest.Status.NEW);return new DashboardStats(total,fresh,total-fresh-done-requests.countByStatus(ProjectRequest.Status.CANCELLED)-requests.countByStatus(ProjectRequest.Status.REJECTED),done,Math.max(0,users.count()-1));}@Transactional @PatchMapping("/requests/{id}/status")public ResponseEntity<ProjectRequest> status(@PathVariable Long id,@Valid @RequestBody StatusUpdate update){return requests.findById(id).map(r->{boolean accepting=update.status()==ProjectRequest.Status.ACCEPTED&&r.getStatus()!=ProjectRequest.Status.ACCEPTED;r.setStatus(update.status());ProjectRequest saved=requests.save(r);if(accepting)mail.sendAccepted(saved);return ResponseEntity.ok(saved);}).orElse(ResponseEntity.notFound().build());}}

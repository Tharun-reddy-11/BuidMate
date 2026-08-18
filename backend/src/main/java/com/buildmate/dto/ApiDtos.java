package com.buildmate.dto;

import com.buildmate.entity.ProjectRequest;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public final class ApiDtos {
  private ApiDtos(){}
  public record LoginRequest(@Email @NotBlank String email, @NotBlank String password){}
  public record AuthResponse(String token, String fullName, String email, String role){}
  public record RequestCreate(Long templateId, @NotBlank String projectTitle, @NotNull ProjectRequest.ProjectType projectType,
    @NotBlank String domain, @Size(max=3000) String description, @NotBlank String teamLeadName,
    @Pattern(regexp="^[0-9+ -]{8,15}$") String phone, @Email @NotBlank String email, @NotBlank String collegeName,
    @NotBlank String rollNumber, @NotBlank String section, @NotBlank String department,
    @Min(1) @Max(20) Integer teamSize, String preferredStack, String budget, LocalDate expectedDeliveryDate){}
  public record StatusUpdate(@NotNull ProjectRequest.Status status){}
  public record DashboardStats(long totalRequests,long newRequests,long activeProjects,long completedProjects,long totalCustomers){}
}

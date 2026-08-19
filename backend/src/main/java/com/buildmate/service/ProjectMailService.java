package com.buildmate.service;

import com.buildmate.entity.ProjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ProjectMailService {
  private final RestClient client;
  private final boolean enabled;
  private final String apiKey;
  private final String fromAddress;
  private final String fromName;

  public ProjectMailService(RestClient.Builder restClientBuilder,
      @Value("${app.mail.enabled}") boolean enabled,
      @Value("${app.mail.brevo-api-key:}") String apiKey,
      @Value("${app.mail.from-email}") String fromAddress,
      @Value("${app.mail.from-name}") String fromName) {
    this.client=restClientBuilder.baseUrl("https://api.brevo.com/v3").build();
    this.enabled=enabled;
    this.apiKey=apiKey;
    this.fromAddress=fromAddress;
    this.fromName=fromName;
  }

  public void sendAccepted(ProjectRequest request) {
    if (!enabled) return;
    if (apiKey.isBlank()) throw new IllegalStateException("Brevo API key is not configured");
    try {
      Map<String,Object> payload=Map.of(
          "sender",Map.of("name",fromName,"email",fromAddress),
          "to",List.of(Map.of("name",request.getTeamLeadName(),"email",request.getEmail())),
          "replyTo",Map.of("name",fromName,"email",fromAddress),
          "subject","Your BuildMate project request has been accepted — "+request.getRequestCode(),
          "htmlContent",html(request));
      client.post()
          .uri("/smtp/email")
          .header("api-key",apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .body(payload)
          .retrieve()
          .toBodilessEntity();
    } catch (Exception ex) {
      throw new IllegalStateException("Request accepted, but the confirmation email could not be sent",ex);
    }
  }

  private String html(ProjectRequest r) {
    return """
      <div style="font-family:Arial,sans-serif;max-width:620px;margin:auto;color:#171717">
        <div style="background:#ff6b35;padding:30px;border-radius:18px 18px 0 0;color:white">
          <h1 style="margin:0">Great news, %s!</h1>
          <p style="margin:10px 0 0">Your project request has been accepted.</p>
        </div>
        <div style="padding:30px;border:1px solid #eee;border-top:0;border-radius:0 0 18px 18px">
          <p>Hi %s,</p>
          <p>Thank you for choosing BuildMate. I’m happy to take your <strong>%s</strong> project forward.</p>
          <div style="background:#f7f4ff;padding:18px;border-radius:12px;margin:22px 0">
            <p style="margin:0 0 8px"><strong>Request ID:</strong> %s</p>
            <p style="margin:0 0 8px"><strong>Domain:</strong> %s</p>
            <p style="margin:0"><strong>Preferred completion:</strong> %s</p>
          </div>
          <p>I’ll contact you shortly to confirm the scope, price, timeline and next steps.</p>
          <p>Warm regards,<br><strong>Tharun</strong><br>BuildMate</p>
        </div>
      </div>
      """.formatted(r.getTeamLeadName(),r.getTeamLeadName(),r.getProjectTitle(),r.getRequestCode(),r.getDomain(),r.getExpectedDeliveryDate()==null?"To be discussed":r.getExpectedDeliveryDate());
  }
}

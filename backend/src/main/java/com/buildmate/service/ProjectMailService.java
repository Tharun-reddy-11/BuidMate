package com.buildmate.service;

import com.buildmate.entity.ProjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class ProjectMailService {
  private static final Logger log=LoggerFactory.getLogger(ProjectMailService.class);
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

  public DeliveryReceipt sendAccepted(ProjectRequest request) {
    if (!enabled) throw new MailDeliveryException("Email delivery is disabled on Render. Set MAIL_ENABLED=true and redeploy.");
    if (apiKey.isBlank()) throw new MailDeliveryException("Brevo API key is missing on Render.");
    if (fromAddress.isBlank()) throw new MailDeliveryException("The Brevo sender email is missing on Render.");
    try {
      Map<String,Object> payload=Map.of(
          "sender",Map.of("name",fromName,"email",fromAddress),
          "to",List.of(Map.of("name",request.getTeamLeadName(),"email",request.getEmail())),
          "replyTo",Map.of("name",fromName,"email",fromAddress),
          "subject","Your BuildMate project request has been accepted — "+request.getRequestCode(),
          "htmlContent",html(request));
      BrevoResponse response=client.post()
          .uri("/smtp/email")
          .header("api-key",apiKey)
          .contentType(MediaType.APPLICATION_JSON)
          .body(payload)
          .retrieve()
          .body(BrevoResponse.class);
      if (response==null || response.messageId()==null || response.messageId().isBlank()) {
        throw new MailDeliveryException("Brevo did not return a delivery message ID.");
      }
      log.info("Acceptance email queued by Brevo: requestCode={}, recipient={}, messageId={}",
          request.getRequestCode(),request.getEmail(),response.messageId());
      return new DeliveryReceipt(response.messageId());
    } catch (MailDeliveryException ex) {
      throw ex;
    } catch (RestClientResponseException ex) {
      log.error("Brevo rejected acceptance email: requestCode={}, status={}, response={}",
          request.getRequestCode(),ex.getStatusCode(),ex.getResponseBodyAsString());
      int status=ex.getStatusCode().value();
      if (status==401 || status==403) {
        throw new MailDeliveryException("Brevo rejected the API key or transactional-email access. Check BREVO_API_KEY and activate transactional email in Brevo.",ex);
      }
      if (status==400) {
        throw new MailDeliveryException("Brevo rejected the sender. Verify MAIL_FROM_EMAIL in Brevo and use the exact same verified address on Render.",ex);
      }
      if (status==402 || status==429) {
        throw new MailDeliveryException("Brevo's daily email allowance is exhausted. Try again after the allowance resets.",ex);
      }
      throw new MailDeliveryException("Brevo could not queue the acceptance email (HTTP "+status+").",ex);
    } catch (Exception ex) {
      log.error("Acceptance email request failed: requestCode={}",request.getRequestCode(),ex);
      throw new MailDeliveryException("The acceptance email could not be sent. Please try again.",ex);
    }
  }

  public record DeliveryReceipt(String messageId) {}
  private record BrevoResponse(String messageId) {}

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

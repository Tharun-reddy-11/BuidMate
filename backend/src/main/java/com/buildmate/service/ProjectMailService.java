package com.buildmate.service;

import com.buildmate.entity.ProjectRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ProjectMailService {
  private final JavaMailSender sender;
  private final boolean enabled;
  private final String fromAddress;
  private final String fromName;

  public ProjectMailService(JavaMailSender sender,
      @Value("${app.mail.enabled}") boolean enabled,
      @Value("${spring.mail.username}") String fromAddress,
      @Value("${app.mail.from-name}") String fromName) {
    this.sender=sender; this.enabled=enabled; this.fromAddress=fromAddress; this.fromName=fromName;
  }

  public void sendAccepted(ProjectRequest request) {
    if (!enabled) return;
    try {
      MimeMessage message=sender.createMimeMessage();
      MimeMessageHelper helper=new MimeMessageHelper(message,"UTF-8");
      helper.setFrom(fromAddress,fromName);
      helper.setTo(request.getEmail());
      helper.setSubject("Your BuildMate project request has been accepted — "+request.getRequestCode());
      helper.setText(html(request),true);
      sender.send(message);
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

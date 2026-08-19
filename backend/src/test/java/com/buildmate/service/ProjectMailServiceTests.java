package com.buildmate.service;

import com.buildmate.entity.ProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ProjectMailServiceTests {
  @Test
  void disabledEmailIsReportedInsteadOfSilentlyAccepting() {
    var service=new ProjectMailService(RestClient.builder(),false,"","owner@example.com","BuildMate");

    assertThrows(MailDeliveryException.class,()->service.sendAccepted(request()));
  }

  @Test
  void successfulBrevoResponseReturnsMessageId() {
    RestClient.Builder builder=RestClient.builder();
    MockRestServiceServer server=MockRestServiceServer.bindTo(builder).build();
    var service=new ProjectMailService(builder,true,"test-api-key","owner@example.com","BuildMate");
    server.expect(requestTo("https://api.brevo.com/v3/smtp/email"))
        .andExpect(header("api-key","test-api-key"))
        .andRespond(withSuccess("{\"messageId\":\"brevo-message-1\"}",MediaType.APPLICATION_JSON));

    var receipt=service.sendAccepted(request());

    assertEquals("brevo-message-1",receipt.messageId());
    server.verify();
  }

  private ProjectRequest request() {
    ProjectRequest request=new ProjectRequest();
    request.setRequestCode("PRJ-TEST-1");
    request.setTeamLeadName("Test User");
    request.setEmail("student@example.com");
    request.setProjectTitle("Test Project");
    request.setDomain("Web Development");
    return request;
  }
}

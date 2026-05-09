package com.event.notifications.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.event.notifications.domain.repository.ActivityEventRepository;
import com.event.notifications.service.mail.NotificationMailer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
class EventSimulationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityEventRepository activityEventRepository;

    @MockitoBean
    private NotificationMailer notificationMailer;

    @Test
    void inviteEventSendsNotificationAndRecordsActivity() throws Exception {
        when(notificationMailer.send(argThat(command -> command.notificationKey().equals("teammate-invited"))))
                .thenReturn(true);

        mockMvc.perform(post("/api/events/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inviterEmail":"alice@acme.test","inviteeName":"Diana Ward","inviteeEmail":"diana@acme.test"}
                                """))
                .andExpect(status().isCreated());

        assertThat(activityEventRepository.count()).isPositive();
    }

    @Test
    void commentEventEmailsTaskOwner() throws Exception {
        when(notificationMailer.send(argThat(command -> command.notificationKey().equals("comment-posted"))))
                .thenReturn(true);

        mockMvc.perform(post("/api/events/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"taskId":1,"authorEmail":"bob@acme.test","body":"This looks good to me."}
                                """))
                .andExpect(status().isCreated());

        verify(notificationMailer).send(argThat(command -> command.notificationKey().equals("comment-posted")
                && command.recipientEmail().equals("alice@acme.test")));
    }

    @Test
    void mailFailureDoesNotBlockDigestBatch() throws Exception {
        when(notificationMailer.send(argThat(command -> command.recipientEmail().equals("alice@acme.test"))))
                .thenReturn(false);

        mockMvc.perform(post("/api/events/invite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inviterEmail":"alice@acme.test","inviteeName":"Eve Stone","inviteeEmail":"eve@acme.test"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/digest/trigger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"days":7}
                                """))
                .andExpect(status().isOk());

        verify(notificationMailer, times(1)).send(argThat(command -> command.notificationKey().equals("weekly-digest")));
    }
}

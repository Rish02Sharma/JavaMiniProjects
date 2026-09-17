package org.example.WebhookDesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebhookController Tests - MockMvc Standalone & Mockito")
class WebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HmacSecurityValidator securityValidator;

    @Mock
    private LeadSyncService leadSyncService;

    @Captor
    private ArgumentCaptor<RequestDto> eventCaptor;

    private static final String VALID_PAYLOAD = """
            {
                "event_id": "evt-12345",
                "customer_id": "cust-987",
                "candidate_id": "cand-555",
                "phone": "+14155552671",
                "language": "en",
                "campaign": "fall_2026",
                "scheduled_at": "2026-10-01T12:00:00Z",
                "metadata": {"source": "web_lead"}
            }
            """;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        WebhookController controller = new WebhookController(
                securityValidator,
                leadSyncService,
                objectMapper,
                validator
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("HMAC Authentication & Authorization")
    class SecurityTests {

        @Test
        @DisplayName("Should return 401 Unauthorized when signature is missing")
        void shouldReturn401WhenSignatureIsMissing() throws Exception {
            byte[] payloadBytes = VALID_PAYLOAD.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), eq(null))).thenReturn(false);

            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(leadSyncService);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when signature does not match payload")
        void shouldReturn401WhenSignatureIsInvalid() throws Exception {
            byte[] payloadBytes = VALID_PAYLOAD.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), eq("invalid-sig"))).thenReturn(false);

            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "invalid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(leadSyncService);
        }
    }

    @Nested
    @DisplayName("Payload Parsing & Validation")
    class PayloadValidationTests {

        @Test
        @DisplayName("Should return 400 Bad Request when payload is malformed JSON")
        void shouldReturn400WhenJsonIsMalformed() throws Exception {
            byte[] malformedJson = "{ unquoted_broken_json : ".getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), any())).thenReturn(true);

            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "valid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(leadSyncService);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when DTO constraint violations occur (invalid phone)")
        void shouldReturn400WhenPhoneViolatesE164() throws Exception {
            String invalidPhoneJson = """
                    {
                        "event_id": "evt-12345",
                        "customer_id": "cust-987",
                        "candidate_id": "cand-555",
                        "phone": "invalid-non-e164",
                        "language": "en",
                        "campaign": "fall_2026",
                        "scheduled_at": "2026-10-01T12:00:00Z"
                    }
                    """;
            byte[] payloadBytes = invalidPhoneJson.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), any())).thenReturn(true);

            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "valid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(leadSyncService);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when required field is missing (null scheduled_at)")
        void shouldReturn400WhenScheduledAtIsMissing() throws Exception {
            String missingTimestampJson = """
                    {
                        "event_id": "evt-12345",
                        "customer_id": "cust-987",
                        "candidate_id": "cand-555",
                        "phone": "+14155552671",
                        "language": "en",
                        "campaign": "fall_2026"
                    }
                    """;
            byte[] payloadBytes = missingTimestampJson.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), any())).thenReturn(true);

            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "valid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(leadSyncService);
        }
    }

    @Nested
    @DisplayName("Successful Ingestion & MDC Observability")
    class IngestionWorkflowTests {

        @Test
        @DisplayName("Should return 202 Accepted, delegate to LeadSyncService, and clear MDC context")
        void shouldReturn202AcceptedAndDelegateToService() throws Exception {
            // Arrange
            byte[] payloadBytes = VALID_PAYLOAD.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), eq("valid-sig"))).thenReturn(true);
            doNothing().when(leadSyncService).processEvent(any());

            // Act
            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "valid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isAccepted());

            // Assert
            verify(leadSyncService, times(1)).processEvent(eventCaptor.capture());
            RequestDto capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.eventId()).isEqualTo("evt-12345");
            assertThat(capturedEvent.customerId()).isEqualTo("cust-987");
            assertThat(capturedEvent.phone()).isEqualTo("+14155552671");

            // Verify MDC was cleared in finally block (no thread-local leakage)
            assertThat(MDC.get("correlationId")).isNull();
            assertThat(MDC.get("customerId")).isNull();
        }

        @Test
        @DisplayName("Should propagate 409 Conflict when LeadSyncService detects concurrent processing")
        void shouldPropagate409ConflictFromService() throws Exception {
            // Arrange
            byte[] payloadBytes = VALID_PAYLOAD.getBytes(StandardCharsets.UTF_8);
            when(securityValidator.isValidSignature(any(), eq("valid-sig"))).thenReturn(true);
            doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Concurrent duplicate event processing"))
                    .when(leadSyncService).processEvent(any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/webhooks/leads")
                            .header("X-Signature-256", "valid-sig")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payloadBytes))
                    .andExpect(status().isConflict());

            // Verify MDC is cleared even when an exception is thrown
            assertThat(MDC.get("correlationId")).isNull();
            assertThat(MDC.get("customerId")).isNull();
        }
    }
}

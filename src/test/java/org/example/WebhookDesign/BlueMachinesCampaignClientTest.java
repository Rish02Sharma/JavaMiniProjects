package org.example.WebhookDesign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@DisplayName("BlueMachinesCampaignClient Tests - Resilience & MockRestServiceServer")
class BlueMachinesCampaignClientTest {

    private static final String BASE_URL = "https://api.bluemachines.ai";
    private static final String API_KEY = "test-bm-api-key";

    private MockRestServiceServer mockServer;
    private BlueMachinesCampaignClient campaignClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        campaignClient = new BlueMachinesCampaignClient(builder, BASE_URL, API_KEY);
    }

    private RequestDto createTestEvent() {
        return new RequestDto(
                "evt-client-1",
                "cust-001",
                "cand-001",
                "+14155552671",
                "en",
                "sales_outreach",
                Instant.parse("2026-10-01T12:00:00Z"),
                Map.of("priority", "high")
        );
    }

    @Nested
    @DisplayName("Successful Invocations")
    class SuccessTests {

        @Test
        @DisplayName("Should send POST request with headers and parse response on first attempt")
        void shouldTriggerCampaignSuccessfullyOnFirstAttempt() {
            // Arrange
            RequestDto event = createTestEvent();
            String responseJson = """
                    {
                        "campaign_id": "bm-camp-999",
                        "status": "QUEUED"
                    }
                    """;

            mockServer.expect(requestTo(BASE_URL + "/v1/campaigns/outbound-call"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(header("Authorization", "Bearer " + API_KEY))
                    .andExpect(header("Content-Type", "application/json"))
                    .andExpect(jsonPath("$.event_id").value("evt-client-1"))
                    .andExpect(jsonPath("$.phone").value("+14155552671"))
                    .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

            // Act
            BlueMachinesCampaignClient.CampaignResponse response = campaignClient.triggerCampaignWithRetry(event);

            // Assert
            mockServer.verify();
            assertThat(response).isNotNull();
            assertThat(response.campaign_id()).isEqualTo("bm-camp-999");
            assertThat(response.status()).isEqualTo("QUEUED");
        }

        @Test
        @DisplayName("Should retry and succeed on second attempt after a transient 500 error")
        void shouldSucceedOnRetryAfterTransientFailure() {
            // Arrange
            RequestDto event = createTestEvent();
            String successJson = """
                    {
                        "campaign_id": "bm-camp-recovered",
                        "status": "QUEUED"
                    }
                    """;

            // First call: 500 Internal Server Error
            mockServer.expect(requestTo(BASE_URL + "/v1/campaigns/outbound-call"))
                    .andRespond(withServerError());

            // Second call: 200 OK
            mockServer.expect(requestTo(BASE_URL + "/v1/campaigns/outbound-call"))
                    .andRespond(withSuccess(successJson, MediaType.APPLICATION_JSON));

            // Act
            BlueMachinesCampaignClient.CampaignResponse response = campaignClient.triggerCampaignWithRetry(event);

            // Assert
            mockServer.verify();
            assertThat(response.campaign_id()).isEqualTo("bm-camp-recovered");
        }
    }

    @Nested
    @DisplayName("Failure & Retry Exhaustion")
    class FailureTests {

        @Test
        @DisplayName("Should throw RateLimitException when 429 is received on all 3 attempts")
        void shouldThrowRateLimitExceptionWhen429Exhausted() {
            // Arrange
            RequestDto event = createTestEvent();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Retry-After", "30");

            mockServer.expect(ExpectedCount.times(3), requestTo(BASE_URL + "/v1/campaigns/outbound-call"))
                    .andRespond(withRawStatus(429).headers(headers));

            // Act & Assert
            assertThatThrownBy(() -> campaignClient.triggerCampaignWithRetry(event))
                    .isInstanceOf(BlueMachinesCampaignClient.RateLimitException.class)
                    .hasMessageContaining("BM API rate limit reached. Retry-After: 30");

            mockServer.verify();
        }

        @Test
        @DisplayName("Should exhaust retries and throw RuntimeException on continuous 500 server errors")
        void shouldThrowExceptionWhenAllRetriesExhausted() {
            // Arrange
            RequestDto event = createTestEvent();

            mockServer.expect(ExpectedCount.times(3), requestTo(BASE_URL + "/v1/campaigns/outbound-call"))
                    .andRespond(withServerError());

            // Act & Assert
            assertThatThrownBy(() -> campaignClient.triggerCampaignWithRetry(event))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Retries exhausted");

            mockServer.verify();
        }
    }
}

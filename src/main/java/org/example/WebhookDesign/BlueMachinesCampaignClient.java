package org.example.WebhookDesign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class BlueMachinesCampaignClient {

    private static final Logger log = LoggerFactory.getLogger(BlueMachinesCampaignClient.class);
    private final RestClient restClient;

    public record CampaignResponse(String campaign_id, String status) {}

    public BlueMachinesCampaignClient(
            RestClient.Builder builder,
            @Value("${bm.client.base-url:https://api.bluemachines.ai}") String baseUrl,
            @Value("${bm.client.api-key:bm_live_key_xyz}") String apiKey) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public CampaignResponse triggerCampaignWithRetry(RequestDto event) {
        int maxAttempts = 3;
        long baseBackoffMs = 400;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restClient.post()
                        .uri("/v1/campaigns/outbound-call")
                        .body(event)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (req, resp) -> {
                            if (resp.getStatusCode().value() == 429) {
                                String retryAfter = resp.getHeaders().getFirst("Retry-After");
                                throw new RateLimitException("BM API rate limit reached. Retry-After: " + retryAfter);
                            }
                            throw new RuntimeException("Client error calling BM API: " + resp.getStatusCode());
                        })
                        .body(CampaignResponse.class);

            } catch (RateLimitException rle) {
                log.warn("Downstream 429 Throttled on attempt {}/{}", attempt, maxAttempts);
                if (attempt == maxAttempts) throw rle;
                sleepWithJitter(baseBackoffMs * (1L << attempt));
            } catch (Exception ex) {
                log.error("Downstream failure calling BM API on attempt {}/{}: {}", attempt, maxAttempts, ex.getMessage());
                if (attempt == maxAttempts) throw new RuntimeException("Retries exhausted", ex);
                sleepWithJitter(baseBackoffMs * (1L << attempt));
            }
        }
        throw new IllegalStateException("Unexpected processing state");
    }

    private void sleepWithJitter(long baseMs) {
        try {
            long jitter = ThreadLocalRandom.current().nextLong(50, 150);
            Thread.sleep(baseMs + jitter);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) { super(message); }
    }
}
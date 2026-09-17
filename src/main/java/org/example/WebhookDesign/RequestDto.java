package org.example.WebhookDesign;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.Map;

public record RequestDto(
        @NotBlank(message = "event_id must not be blank")
        @JsonProperty("event_id")
        String eventId,

        @NotBlank(message = "customer_id must not be blank")
        @JsonProperty("customer_id")
        String customerId,

        @NotBlank(message = "candidate_id must not be blank")
        @JsonProperty("candidate_id")
        String candidateId,

        @NotBlank(message = "phone must not be blank")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "phone must follow E.164 standard")
        @JsonProperty("phone")
        String phone,

        @NotBlank(message = "language must not be blank")
        @JsonProperty("language")
        String language,

        @NotBlank(message = "campaign must not be blank")
        @JsonProperty("campaign")
        String campaign,

        @NotNull(message = "scheduled_at must be provided")
        @JsonProperty("scheduled_at")
        Instant scheduledAt,

        @JsonProperty("metadata")
        Map<String, Object> metadata
) {
}

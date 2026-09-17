package org.example.WebhookDesign;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/webhooks")
@AllArgsConstructor
public class WebhookController {
    private final HmacSecurityValidator securityValidator;
    private final LeadSyncService leadSyncService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @PostMapping(value = "/leads", consumes = "application/json")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "X-Signature-256", required = false) String signature,
            @RequestBody byte[] rawPayload) {

        if (!securityValidator.isValidSignature(rawPayload, signature)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing HMAC signature");
        }

        RequestDto event;
        try {
            event = objectMapper.readValue(rawPayload, RequestDto.class);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Malformed JSON body", e);
        }

        Set<ConstraintViolation<RequestDto>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payload validation failed: " + violations.iterator().next().getMessage());
        }

        // Observability Setup: Structured MDC Logging
        MDC.put("correlationId", event.eventId());
        MDC.put("customerId", event.customerId());
        try {
            leadSyncService.processEvent(event);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // 202 Accepted
        } finally {
            MDC.clear();
        }
    }
}
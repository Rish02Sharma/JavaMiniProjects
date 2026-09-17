package org.example.WebhookDesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestDto Validation & Serialization Tests")
class RequestDtoValidationTest {

    private static Validator validator;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private RequestDto createValidDto() {
        return new RequestDto(
                "evt-12345",
                "cust-987",
                "cand-555",
                "+14155552671",
                "en",
                "fall_2026",
                Instant.parse("2026-10-01T12:00:00Z"),
                Map.of("source", "web_lead")
        );
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWhenValid() {
        RequestDto dto = createValidDto();
        Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Nested
    @DisplayName("E.164 Phone Number Format Tests")
    class PhoneFormatTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "+14155552671",      // US
                "+442071838750",     // UK
                "+919876543210",     // India
                "+33123456789",      // France
                "+81312345678"       // Japan
        })
        @DisplayName("Should accept valid E.164 phone numbers")
        void shouldAcceptValidE164PhoneNumbers(String validPhone) {
            RequestDto dto = new RequestDto(
                    "evt-1", "cust-1", "cand-1", validPhone, "en", "camp", Instant.now(), null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "4155552671",             // Missing leading '+'
                "+0123456789",            // Country code cannot start with 0
                "+1234567890123456",      // Too long (>15 digits standard limit)
                "invalid-phone",          // Non-numeric
                "+",                      // Just plus sign
                "+1 415 555 2671",        // Contains spaces
                "+1-415-555-2671"         // Contains hyphens
        })
        @DisplayName("Should reject invalid phone numbers violating E.164 pattern")
        void shouldRejectInvalidPhoneNumbers(String invalidPhone) {
            RequestDto dto = new RequestDto(
                    "evt-1", "cust-1", "cand-1", invalidPhone, "en", "camp", Instant.now(), null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                    .anyMatch(v -> v.getPropertyPath().toString().equals("phone") &&
                            v.getMessage().contains("E.164"));
        }
    }

    @Nested
    @DisplayName("Mandatory Field Validation")
    class MandatoryFieldTests {

        @Test
        @DisplayName("Should fail when event_id is blank")
        void shouldFailWhenEventIdIsBlank() {
            RequestDto dto = new RequestDto(
                    "", "cust-1", "cand-1", "+14155552671", "en", "camp", Instant.now(), null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("eventId"));
        }

        @Test
        @DisplayName("Should fail when customer_id is blank")
        void shouldFailWhenCustomerIdIsBlank() {
            RequestDto dto = new RequestDto(
                    "evt-1", "   ", "cand-1", "+14155552671", "en", "camp", Instant.now(), null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("customerId"));
        }

        @Test
        @DisplayName("Should fail when candidate_id is null")
        void shouldFailWhenCandidateIdIsNull() {
            RequestDto dto = new RequestDto(
                    "evt-1", "cust-1", null, "+14155552671", "en", "camp", Instant.now(), null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("candidateId"));
        }

        @Test
        @DisplayName("Should fail when scheduled_at is null")
        void shouldFailWhenScheduledAtIsNull() {
            RequestDto dto = new RequestDto(
                    "evt-1", "cust-1", "cand-1", "+14155552671", "en", "camp", null, null
            );
            Set<ConstraintViolation<RequestDto>> violations = validator.validate(dto);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("scheduledAt"));
        }
    }

    @Nested
    @DisplayName("Jackson JSON Deserialization")
    class JsonDeserializationTests {

        @Test
        @DisplayName("Should deserialize valid JSON with snake_case fields and ISO timestamp")
        void shouldDeserializeValidJson() throws Exception {
            String json = """
                    {
                        "event_id": "evt-json-1",
                        "customer_id": "cust-99",
                        "candidate_id": "cand-88",
                        "phone": "+14155552671",
                        "language": "es",
                        "campaign": "summer_sale",
                        "scheduled_at": "2026-11-15T09:30:00Z",
                        "metadata": {
                            "referrer": "linkedin",
                            "score": 95
                        }
                    }
                    """;

            RequestDto dto = objectMapper.readValue(json, RequestDto.class);

            assertThat(dto).isNotNull();
            assertThat(dto.eventId()).isEqualTo("evt-json-1");
            assertThat(dto.customerId()).isEqualTo("cust-99");
            assertThat(dto.candidateId()).isEqualTo("cand-88");
            assertThat(dto.phone()).isEqualTo("+14155552671");
            assertThat(dto.language()).isEqualTo("es");
            assertThat(dto.campaign()).isEqualTo("summer_sale");
            assertThat(dto.scheduledAt()).isEqualTo(Instant.parse("2026-11-15T09:30:00Z"));
            assertThat(dto.metadata()).containsEntry("referrer", "linkedin");
            assertThat(dto.metadata()).containsEntry("score", 95);
        }
    }
}

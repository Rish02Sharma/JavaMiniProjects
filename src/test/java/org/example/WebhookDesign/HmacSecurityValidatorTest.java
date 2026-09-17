package org.example.WebhookDesign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HmacSecurityValidator Tests - HMAC-SHA256 Signature Verification")
class HmacSecurityValidatorTest {

    private static final String SECRET_KEY = "test-secret-32-chars-long-minimum";
    private HmacSecurityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new HmacSecurityValidator(SECRET_KEY);
    }

    private String computeHmac(byte[] payload, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(payload));
    }

    @Nested
    @DisplayName("Valid Signatures")
    class ValidSignatureTests {

        @Test
        @DisplayName("Should return true when valid signature matches raw payload")
        void shouldReturnTrueWhenSignatureMatchesPayload() throws Exception {
            // Arrange
            byte[] payload = "{\"event_id\":\"evt-100\"}".getBytes(StandardCharsets.UTF_8);
            String signature = computeHmac(payload, SECRET_KEY);

            // Act
            boolean result = validator.isValidSignature(payload, signature);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return true and be case-insensitive with whitespace trimmed")
        void shouldReturnTrueWithUppercaseSignatureAndWhitespace() throws Exception {
            // Arrange
            byte[] payload = "{\"event_id\":\"evt-101\"}".getBytes(StandardCharsets.UTF_8);
            String signature = "  " + computeHmac(payload, SECRET_KEY).toUpperCase() + "  ";

            // Act
            boolean result = validator.isValidSignature(payload, signature);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid / Tampered Signatures")
    class InvalidSignatureTests {

        @Test
        @DisplayName("Should return false when payload was tampered with after signing")
        void shouldReturnFalseWhenPayloadIsTampered() throws Exception {
            // Arrange
            byte[] originalPayload = "{\"amount\": 100}".getBytes(StandardCharsets.UTF_8);
            String signature = computeHmac(originalPayload, SECRET_KEY);
            byte[] tamperedPayload = "{\"amount\": 99999}".getBytes(StandardCharsets.UTF_8);

            // Act
            boolean result = validator.isValidSignature(tamperedPayload, signature);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when signature was generated with a different secret key")
        void shouldReturnFalseWhenSignedWithDifferentSecretKey() throws Exception {
            // Arrange
            byte[] payload = "{\"event_id\":\"evt-102\"}".getBytes(StandardCharsets.UTF_8);
            String invalidKeySignature = computeHmac(payload, "different-wrong-secret-key-12345");

            // Act
            boolean result = validator.isValidSignature(payload, invalidKeySignature);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when signature is a completely bogus hex string")
        void shouldReturnFalseWhenSignatureIsRandomHex() {
            // Arrange
            byte[] payload = "{\"event_id\":\"evt-103\"}".getBytes(StandardCharsets.UTF_8);
            String bogusSignature = "deadbeefcafebabe0123456789abcdef";

            // Act
            boolean result = validator.isValidSignature(payload, bogusSignature);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases & Null/Empty Inputs")
    class EdgeCaseTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should return false when signature header is null, empty, or whitespace")
        void shouldReturnFalseWhenSignatureIsNullOrEmpty(String emptyOrNullSignature) {
            // Arrange
            byte[] payload = "{\"event_id\":\"evt-104\"}".getBytes(StandardCharsets.UTF_8);

            // Act
            boolean result = validator.isValidSignature(payload, emptyOrNullSignature);

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when raw payload is null")
        void shouldReturnFalseWhenPayloadIsNull() {
            // Act
            boolean result = validator.isValidSignature(null, "some-signature");

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when raw payload is empty byte array")
        void shouldReturnFalseWhenPayloadIsEmpty() {
            // Act
            boolean result = validator.isValidSignature(new byte[0], "some-signature");

            // Assert
            assertThat(result).isFalse();
        }
    }
}

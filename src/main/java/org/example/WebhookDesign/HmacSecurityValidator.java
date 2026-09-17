package org.example.WebhookDesign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class HmacSecurityValidator {

    private final String secretKey;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public HmacSecurityValidator(@Value("${webhook.security.secret-key:test-secret-32-chars-long-minimum}") String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isValidSignature(byte[] rawPayload, String providedHexSignature) {
        if (providedHexSignature == null || rawPayload == null || rawPayload.length == 0) {
            return false;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] calculatedHash = mac.doFinal(rawPayload);
            String calculatedHex = HexFormat.of().formatHex(calculatedHash);

            // MessageDigest.isEqual prevents timing channel vulnerabilities
            return MessageDigest.isEqual(
                    calculatedHex.getBytes(StandardCharsets.UTF_8),
                    providedHexSignature.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception ex) {
            return false;
        }
    }
}

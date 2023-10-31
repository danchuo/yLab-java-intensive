package org.wallet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * The `JwtTokenUtility` class provides utility methods for creating, validating, and extracting
 * information from JWT (JSON Web Token).
 */
public final class JwtTokenUtility {
  private static final int EXPIRATION_MILLIS = 3600000;
  private static final String SECRET_KEY;
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    SECRET_KEY = loadSecretKeyFromProperties();
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  private JwtTokenUtility() {}

  /**
   * Creates a JWT token with the specified subject.
   *
   * @param subject The subject to include in the token.
   * @return A JWT token as a string.
   */
  public static String createJwtToken(String subject) {
    var now = new Date();
    long currentMillis = now.getTime();

    var tz = TimeZone.getTimeZone("GMT");
    now.setTime(currentMillis - tz.getRawOffset());

    Header header = new Header("HS256", "JWT");
    Payload payload = new Payload(subject, now.getTime() + EXPIRATION_MILLIS);

    try {
      String encodedHeader = encodeBase64URLWithoutPadding(OBJECT_MAPPER.writeValueAsBytes(header));
      String encodedPayload =
          encodeBase64URLWithoutPadding(OBJECT_MAPPER.writeValueAsBytes(payload));

      String dataToSign = encodedHeader + "." + encodedPayload;
      String signature = createHMACSHA256Signature(dataToSign);

      return encodedHeader + "." + encodedPayload + "." + signature;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Validates the authenticity of a JWT token.
   *
   * @param token The JWT token to validate.
   * @return `true` if the token is valid, `false` otherwise.
   */
  public static boolean isValid(String token) {
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
      return false;
    }

    String encodedHeader = parts[0];
    String encodedPayload = parts[1];
    String signature = parts[2];

    try {
      String dataToSign = encodedHeader + "." + encodedPayload;
      String calculatedSignature = createHMACSHA256Signature(dataToSign);

      return calculatedSignature.equals(signature);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Extracts the login information from a JWT token.
   *
   * @param jwtToken The JWT token to extract login information from.
   * @return The login information, or `null` if extraction fails.
   */
  public static String getLogin(String jwtToken) {
    String[] parts = jwtToken.split("\\.");
    if (parts.length != 3) {
      return null;
    }

    String encodedPayload = parts[1];

    try {
      Payload payload = OBJECT_MAPPER.readValue(decodeBase64URL(encodedPayload), Payload.class);
      return payload.login();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static String loadSecretKeyFromProperties() {
    try {
      Properties properties = new Properties();
      ClassLoader classLoader = JwtTokenUtility.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("application.properties");
      properties.load(Objects.requireNonNull(inputStream));
      return properties.getProperty("secretKeyJwt");
    } catch (IOException ignored) {
      return "secretKeyJwt";
    }
  }

  private static String encodeBase64URLWithoutPadding(byte[] data) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
  }

  private static byte[] decodeBase64URL(String data) {
    return Base64.getUrlDecoder().decode(data);
  }

  private static String createHMACSHA256Signature(String data) {
    try {
      Mac hmacSha256 = Mac.getInstance("HmacSHA256");
      byte[] secretBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
      SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
      hmacSha256.init(secretKey);
      byte[] signatureBytes = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return encodeBase64URLWithoutPadding(signatureBytes);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /** Represents the header section of a JWT. */
  public record Header(String alg, String typ) {}

  /** Represents the payload section of a JWT. */
  public record Payload(String login, long exp) {}
}

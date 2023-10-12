package org.wallet.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class StringHasher {

  public static final int CAPACITY = 64;

  private StringHasher() {}

  public static String hashString(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashedBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

      StringBuilder stringBuilder = new StringBuilder(CAPACITY);
      for (byte b : hashedBytes) {
        stringBuilder.append(String.format("%02x", b));
      }

      return stringBuilder.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}

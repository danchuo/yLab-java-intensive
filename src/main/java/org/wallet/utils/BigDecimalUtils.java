package org.wallet.utils;

import java.math.BigDecimal;

public final class BigDecimalUtils {
  private BigDecimalUtils() {}

  public static BigDecimal fromLong(long value) {
    return BigDecimal.valueOf(value);
  }
}

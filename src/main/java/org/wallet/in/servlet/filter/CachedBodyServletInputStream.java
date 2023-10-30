package org.wallet.in.servlet.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * A custom `ServletInputStream` that wraps an input stream for caching the request body. This class
 * is used to provide cached input streams for the body of HTTP requests.
 */
public class CachedBodyServletInputStream extends ServletInputStream {
  private final InputStream cachedBodyInputStream;

  /**
   * Constructs a `CachedBodyServletInputStream` with the provided cached body.
   *
   * @param cachedBody An array of bytes representing the cached request body.
   */
  public CachedBodyServletInputStream(byte... cachedBody) {
    cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
  }

  /**
   * Checks if the stream has finished reading the cached body.
   *
   * @return `true` if the stream has finished reading the cached body, `false` otherwise.
   * @throws IllegalStateException If an I/O error occurs while checking the available bytes.
   */
  @Override
  public boolean isFinished() {
    try {
      return cachedBodyInputStream.available() == 0;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Indicates whether this input stream is ready to be read.
   *
   * @return `true` if the stream is ready for reading, `false` otherwise.
   */
  @Override
  public boolean isReady() {
    return true;
  }

  /**
   * Sets the `ReadListener` for this input stream.
   *
   * @param readListener The `ReadListener` to be set (not used in this implementation).
   */
  @Override
  public void setReadListener(ReadListener readListener) {}

  /**
   * Reads the next byte of data from the input stream.
   *
   * @return The next byte of data or -1 if the end of the input stream is reached.
   * @throws IOException If an I/O error occurs while reading from the input stream.
   */
  @Override
  public int read() throws IOException {
    return cachedBodyInputStream.read();
  }
}

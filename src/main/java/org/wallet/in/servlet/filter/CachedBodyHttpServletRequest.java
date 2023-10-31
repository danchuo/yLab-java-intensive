package org.wallet.in.servlet.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A custom `HttpServletRequestWrapper` that caches the request body for reuse. This class provides
 * methods for copying the request input stream and returning it as a `ServletInputStream` or
 * `BufferedReader`.
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

  public static final int BUFFER_SIZE = 4096;

  private static final byte[] EMPTY_CONTENT = new byte[0];

  private final byte[] cachedBody;

  /**
   * Constructs a `CachedBodyHttpServletRequest` by copying the request's input stream.
   *
   * @param request The original `HttpServletRequest` whose input stream is to be cached.
   * @throws IOException If an I/O error occurs while copying the input stream.
   */
  public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    cachedBody = copyToByteArray(requestInputStream);
  }

  /**
   * Copies the content of an input stream to a byte array.
   *
   * @param in The input stream to be copied.
   * @return A byte array containing the copied content.
   * @throws IOException If an I/O error occurs while copying the stream.
   */
  public static byte[] copyToByteArray(InputStream in) throws IOException {
    if (in == null) {
      return new byte[0];
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
    copy(in, out);
    return out.toByteArray();
  }

  /**
   * Copies data from an input stream to an output stream.
   *
   * @param in The input stream to copy from.
   * @param out The output stream to copy to.
   * @return The total number of bytes copied.
   * @throws IOException If an I/O error occurs during copying.
   */
  public static int copy(InputStream in, OutputStream out) throws IOException {
    int byteCount = 0;
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytesRead = -1;
    while ((bytesRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, bytesRead);
      byteCount += bytesRead;
    }
    out.flush();
    return byteCount;
  }

  /**
   * Returns a `ServletInputStream` for the cached request body.
   *
   * @return A `ServletInputStream` containing the cached request body.
   * @throws IOException If an I/O error occurs while creating the `ServletInputStream`.
   */
  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  /**
   * Returns a `BufferedReader` for the cached request body.
   *
   * @return A `BufferedReader` containing the cached request body.
   * @throws IOException If an I/O error occurs while creating the `BufferedReader`.
   */
  @Override
  public BufferedReader getReader() throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
  }
}

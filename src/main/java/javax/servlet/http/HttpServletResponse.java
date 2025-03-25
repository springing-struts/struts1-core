package javax.servlet.http;

import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Collection;
import javax.servlet.ServletResponse;

public interface HttpServletResponse
  extends ServletResponse, jakarta.servlet.http.HttpServletResponse {
  default jakarta.servlet.http.HttpServletResponse unwrap() {
    throw new UnsupportedOperationException();
  }

  static HttpServletResponse toJavaxNamespace(
    jakarta.servlet.ServletResponse response
  ) {
    if (
      response instanceof HttpServletResponse inJavaxNamespace &&
      !Proxy.isProxyClass(response.getClass())
    ) {
      return inJavaxNamespace;
    }
    return new Wrapper((jakarta.servlet.http.HttpServletResponse) response);
  }

  class Wrapper extends ServletResponse.Wrapper implements HttpServletResponse {

    public Wrapper(jakarta.servlet.http.HttpServletResponse response) {
      super(response);
      orig = response;
    }

    private final jakarta.servlet.http.HttpServletResponse orig;

    @Override
    public jakarta.servlet.http.HttpServletResponse unwrap() {
      return orig;
    }

    @Override
    public void addCookie(Cookie cookie) {
      orig.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String s) {
      return orig.containsHeader(s);
    }

    @Override
    public String encodeURL(String s) {
      return orig.encodeURL(s);
    }

    @Override
    public String encodeRedirectURL(String s) {
      return orig.encodeRedirectURL(s);
    }

    @Override
    public void sendError(int i, String s) throws IOException {
      orig.sendError(i, s);
    }

    @Override
    public void sendError(int i) throws IOException {
      orig.sendError(i);
    }

    @Override
    public void sendRedirect(String s) throws IOException {
      orig.sendRedirect(s);
    }

    @Override
    public void setDateHeader(String s, long l) {
      orig.setDateHeader(s, l);
    }

    @Override
    public void addDateHeader(String s, long l) {
      orig.addDateHeader(s, l);
    }

    @Override
    public void setHeader(String s, String s1) {
      orig.setHeader(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
      orig.addHeader(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {
      orig.setDateHeader(s, i);
    }

    @Override
    public void addIntHeader(String s, int i) {
      orig.addIntHeader(s, i);
    }

    @Override
    public void setStatus(int i) {
      orig.setStatus(i);
    }

    @Override
    public int getStatus() {
      return orig.getStatus();
    }

    @Override
    public String getHeader(String s) {
      return orig.getHeader(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
      return orig.getHeaders(s);
    }

    @Override
    public Collection<String> getHeaderNames() {
      return orig.getHeaderNames();
    }
  }
}

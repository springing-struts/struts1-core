package javax.servlet;

import jakarta.servlet.*;
import jakarta.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import org.springframework.lang.Nullable;

public interface ServletRequest extends jakarta.servlet.ServletRequest {
  @Override
  @Nullable
  RequestDispatcher getRequestDispatcher(String path);

  default jakarta.servlet.ServletRequest unwrap() {
    throw new UnsupportedOperationException();
  }

  class JavaxNamespaceWrapper implements ServletRequest {

    public JavaxNamespaceWrapper(jakarta.servlet.ServletRequest request) {
      orig = request;
    }

    private final jakarta.servlet.ServletRequest orig;

    @Override
    public jakarta.servlet.ServletRequest unwrap() {
      return orig;
    }

    @Override
    public Object getAttribute(String s) {
      return orig.getAttribute(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
      return orig.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
      return orig.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String s)
      throws UnsupportedEncodingException {
      orig.setCharacterEncoding(s);
    }

    @Override
    public int getContentLength() {
      return orig.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
      return orig.getContentLengthLong();
    }

    @Override
    public String getContentType() {
      return orig.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      return orig.getInputStream();
    }

    @Override
    public String getParameter(String s) {
      return orig.getParameter(s);
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return orig.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String s) {
      return orig.getParameterValues(s);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return orig.getParameterMap();
    }

    @Override
    public String getProtocol() {
      return orig.getProtocol();
    }

    @Override
    public String getScheme() {
      return orig.getScheme();
    }

    @Override
    public String getServerName() {
      return orig.getServerName();
    }

    @Override
    public int getServerPort() {
      return orig.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
      return orig.getReader();
    }

    @Override
    public String getRemoteAddr() {
      return orig.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
      return orig.getRemoteHost();
    }

    @Override
    public void setAttribute(String s, Object o) {
      orig.setAttribute(s, o);
    }

    @Override
    public void removeAttribute(String s) {
      orig.removeAttribute(s);
    }

    @Override
    public Locale getLocale() {
      return orig.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
      return orig.getLocales();
    }

    @Override
    public boolean isSecure() {
      return orig.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
      return RequestDispatcher.wrap(orig.getRequestDispatcher(s));
    }

    @Override
    public int getRemotePort() {
      return orig.getRemotePort();
    }

    @Override
    public String getLocalName() {
      return orig.getLocalName();
    }

    @Override
    public String getLocalAddr() {
      return orig.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
      return orig.getLocalPort();
    }

    @Override
    public javax.servlet.ServletContext getServletContext() {
      return javax.servlet.ServletContext.toJavaxNamespace(
        orig.getServletContext()
      );
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
      return orig.startAsync();
    }

    @Override
    public AsyncContext startAsync(
      jakarta.servlet.ServletRequest servletRequest,
      ServletResponse servletResponse
    ) throws IllegalStateException {
      return orig.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
      return orig.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
      return orig.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
      return orig.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
      return orig.getDispatcherType();
    }

    @Override
    public String getRequestId() {
      return orig.getRequestId();
    }

    @Override
    public String getProtocolRequestId() {
      return orig.getProtocolRequestId();
    }

    @Override
    public ServletConnection getServletConnection() {
      return orig.getServletConnection();
    }
  }
}

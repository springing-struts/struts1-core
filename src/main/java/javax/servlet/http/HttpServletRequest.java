package javax.servlet.http;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.lang.Nullable;

public interface HttpServletRequest
  extends
    javax.servlet.ServletRequest, jakarta.servlet.http.HttpServletRequest {
  @Override
  HttpSession getSession();

  @Override
  HttpSession getSession(boolean create);

  default jakarta.servlet.http.HttpServletRequest unwrap() {
    throw new UnsupportedOperationException();
  }

  static HttpServletRequest toJavaxNamespace(
    jakarta.servlet.ServletRequest request
  ) {
    if (
      request instanceof HttpServletRequest inJavaxNamespace &&
      !Proxy.isProxyClass(request.getClass())
    ) {
      return inJavaxNamespace;
    }
    return new Wrapper((jakarta.servlet.http.HttpServletRequest) request);
  }

  class Wrapper
    extends javax.servlet.ServletRequest.JavaxNamespaceWrapper
    implements HttpServletRequest {

    public Wrapper(jakarta.servlet.http.HttpServletRequest request) {
      super(request);
      orig = request;
    }

    private final jakarta.servlet.http.HttpServletRequest orig;

    @Override
    public jakarta.servlet.http.HttpServletRequest unwrap() {
      if (Proxy.isProxyClass(orig.getClass())) {
        return (jakarta.servlet.http.HttpServletRequest) AopProxyUtils.getSingletonTarget(
          orig
        );
      }
      return orig;
    }

    @Override
    public String getAuthType() {
      return orig.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
      return orig.getCookies();
    }

    @Override
    public long getDateHeader(String s) {
      return orig.getDateHeader(s);
    }

    @Override
    public String getHeader(String s) {
      return orig.getHeader(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
      return orig.getHeaders(s);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
      return orig.getHeaderNames();
    }

    @Override
    public int getIntHeader(String s) {
      return orig.getIntHeader(s);
    }

    @Override
    public String getMethod() {
      return orig.getMethod();
    }

    @Override
    public String getPathInfo() {
      return orig.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
      return orig.getPathTranslated();
    }

    @Override
    public String getContextPath() {
      return orig.getContextPath();
    }

    @Override
    public String getQueryString() {
      return orig.getQueryString();
    }

    @Override
    public String getRemoteUser() {
      return orig.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String s) {
      return orig.isUserInRole(s);
    }

    @Override
    public Principal getUserPrincipal() {
      return orig.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
      return orig.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
      return orig.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
      return orig.getRequestURL();
    }

    @Override
    public String getServletPath() {
      return orig.getServletPath();
    }

    @Override
    public @Nullable HttpSession getSession(boolean b) {
      var session = orig.getSession(b);
      return (session == null) ? null : HttpSession.toJavaxNamespace(session);
    }

    @Override
    public HttpSession getSession() {
      return HttpSession.toJavaxNamespace(orig.getSession());
    }

    @Override
    public String changeSessionId() {
      return orig.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
      return orig.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
      return orig.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
      return orig.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(
      jakarta.servlet.http.HttpServletResponse httpServletResponse
    ) throws IOException, ServletException {
      return orig.authenticate(httpServletResponse);
    }

    @Override
    public void login(String s, String s1) throws ServletException {
      orig.login(s, s1);
    }

    @Override
    public void logout() throws ServletException {
      orig.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
      return orig.getParts();
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
      return orig.getPart(s);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass)
      throws IOException, ServletException {
      return orig.upgrade(aClass);
    }
  }
}

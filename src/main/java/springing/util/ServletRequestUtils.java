package springing.util;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import jakarta.servlet.jsp.PageContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.tags.NestedPathTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.apache.struts.taglib.html.Constants.isReservedFormPropertyName;
import static springing.util.ObjectUtils.*;

public class ServletRequestUtils {

  private ServletRequestUtils() {}

  public static void initialize(jakarta.servlet.http.HttpServletRequest request) {
    ServletRequestUtils.REQUEST = wrap(request);
  }
  private static @Nullable HttpServletRequest REQUEST;

  public static HttpServletRequest getCurrent() {
    if (REQUEST == null) throw new IllegalStateException(
      "The ServletRequestUtils should be initialized before being used."
    );
    return REQUEST;
  }

  public static @Nullable Object getAttributeFromScope(
      PageContext pageContext,
      @Nullable String key
  ) {
    if (key == null) {
      return null;
    }
    var m = INDEXED_PROPERTY.matcher(key);
    var isIndexed = m.matches();
    var index = isIndexed ? Integer.valueOf(m.group(2)) : null;
    var name = isIndexed ? m.group(1) : key;
    var model = pageContext.getAttribute(name);
    if (model != null) {
      return getAt(model, index);
    }
    return getAt(RequestContextHolder
      .currentRequestAttributes()
      .getAttribute(key, RequestAttributes.SCOPE_REQUEST), index);
  }

  public static @Nullable Object resolveValueOnScope(
    @Nullable String attributeName,
    @Nullable String relPath,
    boolean awareNestedTag,
    PageContext pageContext
  ) {
    if (attributeName != null) {
      var bean = getAttributeFromScope(pageContext, attributeName);
      if (relPath == null) return bean;

      return retrieveValue(bean, awareNestedTag ? resolveNestedPath(relPath, pageContext) : relPath);
    }
    if (relPath == null) {
      return null;
    }
    var fullPath = resolveNestedPath(relPath, pageContext);
    var pos = fullPath.indexOf(".");
    var attr = pos < 0 ? fullPath : fullPath.substring(0, pos);
    var propertyPath = pos < 0 ? null : relPath.substring(pos + 1);
    var bean = getAttributeFromScope(pageContext, attr);
    return retrieveValue(bean, propertyPath);
  }

  public static String resolveNestedPath(
    String relPath,
    PageContext pageContext
  ) {
    var nestedPath = (String) pageContext.getAttribute(
      NestedPathTag.NESTED_PATH_VARIABLE_NAME, PageContext.REQUEST_SCOPE
    );
    var path = (nestedPath == null ? "" : (nestedPath + ".")) + relPath;
    return path
        .replaceAll("(\\.{2,})", ".")
        .replaceAll("\\.$", "");
  }

  public static HttpServletRequest wrap(jakarta.servlet.http.HttpServletRequest orig) {
    return new HttpServletRequest() {
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
      public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
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
        return orig.getRequestDispatcher(s);
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
      public ServletContext getServletContext() {
        return orig.getServletContext();
      }

      @Override
      public AsyncContext startAsync() throws IllegalStateException {
        return orig.startAsync();
      }

      @Override
      public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
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
      public HttpSession getSession(boolean b) {
        return HttpSession.wrap(orig.getSession(b));
      }

      @Override
      public HttpSession getSession() {
        return HttpSession.wrap(orig.getSession());
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
      public boolean authenticate(jakarta.servlet.http.HttpServletResponse httpServletResponse) throws IOException, ServletException {
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
      public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return orig.upgrade(aClass);
      }
    };
  }

  public static HttpServletResponse wrap(jakarta.servlet.http.HttpServletResponse orig) {
    return new HttpServletResponse() {
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

      @Override
      public String getCharacterEncoding() {
        return orig.getCharacterEncoding();
      }

      @Override
      public String getContentType() {
        return orig.getContentType();
      }

      @Override
      public ServletOutputStream getOutputStream() throws IOException {
        return orig.getOutputStream();
      }

      @Override
      public PrintWriter getWriter() throws IOException {
        return orig.getWriter();
      }

      @Override
      public void setCharacterEncoding(String s) {
        orig.setCharacterEncoding(s);
      }

      @Override
      public void setContentLength(int i) {
        orig.setContentLength(i);
      }

      @Override
      public void setContentLengthLong(long l) {
        orig.setContentLengthLong(l);
      }

      @Override
      public void setContentType(String s) {
        orig.setContentType(s);
      }

      @Override
      public void setBufferSize(int i) {
        orig.setBufferSize(i);
      }

      @Override
      public int getBufferSize() {
        return orig.getBufferSize();
      }

      @Override
      public void flushBuffer() throws IOException {
        orig.flushBuffer();
      }

      @Override
      public void resetBuffer() {
        orig.resetBuffer();
      }

      @Override
      public boolean isCommitted() {
        return orig.isCommitted();
      }

      @Override
      public void reset() {
        orig.reset();
      }

      @Override
      public void setLocale(Locale locale) {
        orig.setLocale(locale);
      }

      @Override
      public Locale getLocale() {
        return orig.getLocale();
      }
    };
  }
}

package javax.servlet.http;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public interface HttpServletRequest extends jakarta.servlet.http.HttpServletRequest {

  @Override
  HttpSession getSession();

  @Override
  HttpSession getSession(boolean create);

  static HttpServletRequest wrap(jakarta.servlet.http.HttpServletRequest orig) {
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
}

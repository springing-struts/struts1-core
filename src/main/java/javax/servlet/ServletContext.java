package javax.servlet;

import jakarta.servlet.*;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.descriptor.JspConfigDescriptor;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

public interface ServletContext extends jakarta.servlet.ServletContext {
  static ServletContext wrap(jakarta.servlet.ServletContext orig) {
    return new ServletContext() {

      @Override
      public String getContextPath() {
        return orig.getContextPath();
      }

      @Override
      public ServletContext getContext(String uripath) {
        return wrap(orig.getContext(uripath));
      }

      @Override
      public int getMajorVersion() {
        return orig.getMajorVersion();
      }

      @Override
      public int getMinorVersion() {
        return orig.getMinorVersion();
      }

      @Override
      public int getEffectiveMajorVersion() {
        return orig.getEffectiveMajorVersion();
      }

      @Override
      public int getEffectiveMinorVersion() {
        return orig.getEffectiveMinorVersion();
      }

      @Override
      public String getMimeType(String file) {
        return orig.getMimeType(file);
      }

      @Override
      public Set<String> getResourcePaths(String path) {
        return orig.getResourcePaths(path);
      }

      @Override
      public URL getResource(String path) throws MalformedURLException {
        return orig.getResource(path);
      }

      @Override
      public InputStream getResourceAsStream(String path) {
        return orig.getResourceAsStream(path);
      }

      @Override
      public RequestDispatcher getRequestDispatcher(String path) {
        return RequestDispatcher.wrap(orig.getRequestDispatcher(path));
      }

      @Override
      public RequestDispatcher getNamedDispatcher(String name) {
        return RequestDispatcher.wrap(orig.getNamedDispatcher(name));
      }

      @Override
      public void log(String msg) {
        orig.log(msg);
      }

      @Override
      public void log(String message, Throwable throwable) {
        orig.log(message, throwable);
      }

      @Override
      public String getRealPath(String path) {
        return orig.getRealPath(path);
      }

      @Override
      public String getServerInfo() {
        return orig.getServerInfo();
      }

      @Override
      public String getInitParameter(String name) {
        return orig.getInitParameter(name);
      }

      @Override
      public Enumeration<String> getInitParameterNames() {
        return orig.getInitParameterNames();
      }

      @Override
      public boolean setInitParameter(String name, String value) {
        return orig.setInitParameter(name, value);
      }

      @Override
      public Object getAttribute(String name) {
        return orig.getAttribute(name);
      }

      @Override
      public Enumeration<String> getAttributeNames() {
        return orig.getAttributeNames();
      }

      @Override
      public void setAttribute(String name, Object object) {
        orig.setAttribute(name, object);
      }

      @Override
      public void removeAttribute(String name) {
        orig.removeAttribute(name);
      }

      @Override
      public String getServletContextName() {
        return orig.getServletContextName();
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return orig.addServlet(servletName, className);
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return orig.addServlet(servletName, servlet);
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return orig.addServlet(servletName, servletClass);
      }

      @Override
      public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        return orig.addJspFile(jspName, jspFile);
      }

      @Override
      public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        return orig.createServlet(c);
      }

      @Override
      public ServletRegistration getServletRegistration(String servletName) {
        return orig.getServletRegistration(servletName);
      }

      @Override
      public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return orig.getServletRegistrations();
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return orig.addFilter(filterName, className);
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return orig.addFilter(filterName, filter);
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return orig.addFilter(filterName, filterClass);
      }

      @Override
      public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        return orig.createFilter(c);
      }

      @Override
      public FilterRegistration getFilterRegistration(String filterName) {
        return orig.getFilterRegistration(filterName);
      }

      @Override
      public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return orig.getFilterRegistrations();
      }

      @Override
      public SessionCookieConfig getSessionCookieConfig() {
        return orig.getSessionCookieConfig();
      }

      @Override
      public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        orig.setSessionTrackingModes(sessionTrackingModes);
      }

      @Override
      public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return orig.getDefaultSessionTrackingModes();
      }

      @Override
      public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return orig.getEffectiveSessionTrackingModes();
      }

      @Override
      public void addListener(String className) {
        orig.addListener(className);
      }

      @Override
      public <T extends EventListener> void addListener(T t) {
        orig.addListener(t);
      }

      @Override
      public void addListener(Class<? extends EventListener> listenerClass) {
        orig.addListener(listenerClass);
      }

      @Override
      public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        return orig.createListener(c);
      }

      @Override
      public JspConfigDescriptor getJspConfigDescriptor() {
        return orig.getJspConfigDescriptor();
      }

      @Override
      public ClassLoader getClassLoader() {
        return orig.getClassLoader();
      }

      @Override
      public void declareRoles(String... roleNames) {
        orig.declareRoles(roleNames);
      }

      @Override
      public String getVirtualServerName() {
        return orig.getVirtualServerName();
      }

      @Override
      public int getSessionTimeout() {
        return orig.getSessionTimeout();
      }

      @Override
      public void setSessionTimeout(int sessionTimeout) {
        orig.setSessionTimeout(sessionTimeout);
      }

      @Override
      public String getRequestCharacterEncoding() {
        return orig.getRequestCharacterEncoding();
      }

      @Override
      public void setRequestCharacterEncoding(String encoding) {
        orig.setRequestCharacterEncoding(encoding);
      }

      @Override
      public String getResponseCharacterEncoding() {
        return orig.getResponseCharacterEncoding();
      }

      @Override
      public void setResponseCharacterEncoding(String encoding) {
        orig.setResponseCharacterEncoding(encoding);
      }
    };
  }
}

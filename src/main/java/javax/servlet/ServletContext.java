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
  static ServletContext toJavaxNamespace(jakarta.servlet.ServletContext jakarta) {
    return new ServletContext() {

      @Override
      public String getContextPath() {
        return jakarta.getContextPath();
      }

      @Override
      public ServletContext getContext(String uripath) {
        return toJavaxNamespace(jakarta.getContext(uripath));
      }

      @Override
      public int getMajorVersion() {
        return jakarta.getMajorVersion();
      }

      @Override
      public int getMinorVersion() {
        return jakarta.getMinorVersion();
      }

      @Override
      public int getEffectiveMajorVersion() {
        return jakarta.getEffectiveMajorVersion();
      }

      @Override
      public int getEffectiveMinorVersion() {
        return jakarta.getEffectiveMinorVersion();
      }

      @Override
      public String getMimeType(String file) {
        return jakarta.getMimeType(file);
      }

      @Override
      public Set<String> getResourcePaths(String path) {
        return jakarta.getResourcePaths(path);
      }

      @Override
      public URL getResource(String path) throws MalformedURLException {
        return jakarta.getResource(path);
      }

      @Override
      public InputStream getResourceAsStream(String path) {
        return jakarta.getResourceAsStream(path);
      }

      @Override
      public RequestDispatcher getRequestDispatcher(String path) {
        return RequestDispatcher.wrap(jakarta.getRequestDispatcher(path));
      }

      @Override
      public RequestDispatcher getNamedDispatcher(String name) {
        return RequestDispatcher.wrap(jakarta.getNamedDispatcher(name));
      }

      @Override
      public void log(String msg) {
        jakarta.log(msg);
      }

      @Override
      public void log(String message, Throwable throwable) {
        jakarta.log(message, throwable);
      }

      @Override
      public String getRealPath(String path) {
        return jakarta.getRealPath(path);
      }

      @Override
      public String getServerInfo() {
        return jakarta.getServerInfo();
      }

      @Override
      public String getInitParameter(String name) {
        return jakarta.getInitParameter(name);
      }

      @Override
      public Enumeration<String> getInitParameterNames() {
        return jakarta.getInitParameterNames();
      }

      @Override
      public boolean setInitParameter(String name, String value) {
        return jakarta.setInitParameter(name, value);
      }

      @Override
      public Object getAttribute(String name) {
        return jakarta.getAttribute(name);
      }

      @Override
      public Enumeration<String> getAttributeNames() {
        return jakarta.getAttributeNames();
      }

      @Override
      public void setAttribute(String name, Object object) {
        jakarta.setAttribute(name, object);
      }

      @Override
      public void removeAttribute(String name) {
        jakarta.removeAttribute(name);
      }

      @Override
      public String getServletContextName() {
        return jakarta.getServletContextName();
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, String className) {
        return jakarta.addServlet(servletName, className);
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, Servlet servlet) {
        return jakarta.addServlet(servletName, servlet);
      }

      @Override
      public ServletRegistration.Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass) {
        return jakarta.addServlet(servletName, servletClass);
      }

      @Override
      public ServletRegistration.Dynamic addJspFile(String jspName, String jspFile) {
        return jakarta.addJspFile(jspName, jspFile);
      }

      @Override
      public <T extends Servlet> T createServlet(Class<T> c) throws ServletException {
        return jakarta.createServlet(c);
      }

      @Override
      public ServletRegistration getServletRegistration(String servletName) {
        return jakarta.getServletRegistration(servletName);
      }

      @Override
      public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return jakarta.getServletRegistrations();
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, String className) {
        return jakarta.addFilter(filterName, className);
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
        return jakarta.addFilter(filterName, filter);
      }

      @Override
      public FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass) {
        return jakarta.addFilter(filterName, filterClass);
      }

      @Override
      public <T extends Filter> T createFilter(Class<T> c) throws ServletException {
        return jakarta.createFilter(c);
      }

      @Override
      public FilterRegistration getFilterRegistration(String filterName) {
        return jakarta.getFilterRegistration(filterName);
      }

      @Override
      public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return jakarta.getFilterRegistrations();
      }

      @Override
      public SessionCookieConfig getSessionCookieConfig() {
        return jakarta.getSessionCookieConfig();
      }

      @Override
      public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
        jakarta.setSessionTrackingModes(sessionTrackingModes);
      }

      @Override
      public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return jakarta.getDefaultSessionTrackingModes();
      }

      @Override
      public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return jakarta.getEffectiveSessionTrackingModes();
      }

      @Override
      public void addListener(String className) {
        jakarta.addListener(className);
      }

      @Override
      public <T extends EventListener> void addListener(T t) {
        jakarta.addListener(t);
      }

      @Override
      public void addListener(Class<? extends EventListener> listenerClass) {
        jakarta.addListener(listenerClass);
      }

      @Override
      public <T extends EventListener> T createListener(Class<T> c) throws ServletException {
        return jakarta.createListener(c);
      }

      @Override
      public JspConfigDescriptor getJspConfigDescriptor() {
        return jakarta.getJspConfigDescriptor();
      }

      @Override
      public ClassLoader getClassLoader() {
        return jakarta.getClassLoader();
      }

      @Override
      public void declareRoles(String... roleNames) {
        jakarta.declareRoles(roleNames);
      }

      @Override
      public String getVirtualServerName() {
        return jakarta.getVirtualServerName();
      }

      @Override
      public int getSessionTimeout() {
        return jakarta.getSessionTimeout();
      }

      @Override
      public void setSessionTimeout(int sessionTimeout) {
        jakarta.setSessionTimeout(sessionTimeout);
      }

      @Override
      public String getRequestCharacterEncoding() {
        return jakarta.getRequestCharacterEncoding();
      }

      @Override
      public void setRequestCharacterEncoding(String encoding) {
        jakarta.setRequestCharacterEncoding(encoding);
      }

      @Override
      public String getResponseCharacterEncoding() {
        return jakarta.getResponseCharacterEncoding();
      }

      @Override
      public void setResponseCharacterEncoding(String encoding) {
        jakarta.setResponseCharacterEncoding(encoding);
      }
    };
  }
}

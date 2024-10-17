package javax.servlet;

import java.util.Enumeration;

public interface ServletConfig extends jakarta.servlet.ServletConfig {
  @Override
  ServletContext getServletContext();

  static ServletConfig wrap(jakarta.servlet.ServletConfig servletConfig) {
    return new ServletConfig() {
      @Override
      public String getServletName() {
        return servletConfig.getServletName();
      }

      @Override
      public ServletContext getServletContext() {
        return ServletContext.wrap(servletConfig.getServletContext());
      }

      @Override
      public String getInitParameter(String name) {
        return servletConfig.getInitParameter(name);
      }

      @Override
      public Enumeration<String> getInitParameterNames() {
        return servletConfig.getInitParameterNames();
      }
    };
  }
}

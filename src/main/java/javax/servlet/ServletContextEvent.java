package javax.servlet;

import java.util.EventObject;

public class ServletContextEvent extends EventObject {

  public ServletContextEvent(ServletContext source) {
    super(source);
  }

  public ServletContext getServletContext() {
    return (ServletContext) super.getSource();
  }

  static ServletContextEvent wrap(jakarta.servlet.ServletContextEvent orig) {
    return new ServletContextEvent(
      ServletContext.toJavaxNamespace(orig.getServletContext())
    ) {};
  }
}

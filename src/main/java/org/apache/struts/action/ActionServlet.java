package org.apache.struts.action;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Dummy implementation of a Struts Action Servlet for Action classes which
 * directly access the instance.
 */
public class ActionServlet extends HttpServlet {

  @Override
  public ServletContext getServletContext() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
      return attrs.getRequest().getServletContext();
    }
    throw new IllegalStateException(
      "Failed to retrieve the servlet context for the current request."
    );
  }
}

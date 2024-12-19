package springing.struts1.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import java.io.IOException;

import static jakarta.servlet.DispatcherType.INCLUDE;
import static java.util.Objects.requireNonNull;

public class JspForwardingHandler extends DefaultServletHttpRequestHandler {
  public JspForwardingHandler(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  private final ServletContext servletContext;

  @Override
  public void handleRequest(
      HttpServletRequest request, HttpServletResponse response
  ) throws ServletException, IOException {
    if (request.getDispatcherType() == INCLUDE) {
      handleInclude(request, response);
    }
    else {
      handleForward(request, response);
    }
  }

  private void handleForward(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    var dispatcher = requireNonNull(
      servletContext.getNamedDispatcher("jsp"),
      "A RequestDispatcher could not be located for the default servlet: jsp."
    );
    dispatcher.forward(request, response);
  }

  private void handleInclude(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    var dispatchPath = getDispatchPath(request);
    var dispatcher = requireNonNull(
      servletContext.getNamedDispatcher("jsp"),
      "A RequestDispatcher could not be located for the default servlet: jsp."
    );
    request.setAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH, dispatchPath);
    dispatcher.include(new HttpServletRequestWrapper(request) {
      @Override
      public Object getAttribute(String name) {
        if (Globals.DISPATCHER_REQUEST_PATH_ATTR.equals(name) || RequestDispatcher.INCLUDE_SERVLET_PATH.equals(name)) {
          return dispatchPath;
        }
        return super.getAttribute(name);
      }
    }, response);
  }

  private String getDispatchPath(HttpServletRequest request) {
    return requireNonNull(
      (String) request.getAttribute(Globals.DISPATCHER_REQUEST_PATH_ATTR),
      "Failed to determine the dispatch path for the include request."
    );
  }
}

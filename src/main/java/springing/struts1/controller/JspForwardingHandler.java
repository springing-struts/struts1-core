package springing.struts1.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;

import static jakarta.servlet.DispatcherType.INCLUDE;
import static java.util.Objects.requireNonNull;

public class JspForwardingHandler implements HttpRequestHandler {

  @Override
  public void handleRequest(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    if (request.getDispatcherType() == INCLUDE) {
      handleInclude(request, response);
    }
    else {
      handleForward(request, response);
    }
  }

  private RequestDispatcher getDispatcher(HttpServletRequest request) {
    return requireNonNull(
      request.getServletContext().getNamedDispatcher("jsp"),
      "A RequestDispatcher could not be located for the default servlet: jsp."
    );
  }

  private void handleForward(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    getDispatcher(request).forward(request, response);
  }

  private void handleInclude(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    var dispatchPath = getDispatchPath(request);
    request.setAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH, dispatchPath);
    getDispatcher(request).include(new HttpServletRequestWrapper(request) {
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

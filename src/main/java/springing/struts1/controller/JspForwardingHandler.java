package springing.struts1.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

import java.io.IOException;

import static jakarta.servlet.DispatcherType.INCLUDE;
import static org.springframework.util.StringUtils.hasText;

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
    RequestDispatcher rd = servletContext.getNamedDispatcher("jsp");
    if (rd == null) throw new IllegalStateException(
        "A RequestDispatcher could not be located for the default servlet: jsp."
    );
    rd.forward(request, response);
  }

  private void handleInclude(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    var dispatchPath = (String) request.getAttribute(DISPATCHER_REQUEST_PATH);
    if (!hasText(dispatchPath)) throw new IllegalStateException(
      "Failed to determine the dispatch path for the include request."
    );
    var rd = request.getRequestDispatcher(dispatchPath);
    rd.forward(request, response);
  }

  private static final String DISPATCHER_REQUEST_PATH = "org.apache.catalina.core.DISPATCHER_REQUEST_PATH";
}

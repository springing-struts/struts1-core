package springing.struts1.controller;

import static jakarta.servlet.DispatcherType.INCLUDE;
import static java.util.Objects.requireNonNull;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.catalina.core.RequestWrapper;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

public class JspForwardingHandler implements HttpRequestHandler {

  public JspForwardingHandler(
    ResourceHttpRequestHandler staticResourceHandler
  ) {
    this.staticResourceHandler = staticResourceHandler;
  }

  private final ResourceHttpRequestHandler staticResourceHandler;

  @Override
  public void handleRequest(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws ServletException, IOException {
    var requestUri = request.getRequestURI();
    var isJspResource = requestUri.endsWith(".jsp");
    if (!isJspResource) {
      staticResourceHandler.handleRequest(request, response);
      return;
    }
    if (request.getDispatcherType() == INCLUDE) {
      handleInclude(request, response);
    } else {
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
    getDispatcher(request).include(new RequestWrapper(request), response);
  }
}

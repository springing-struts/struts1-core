package org.apache.catalina.core;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

public class RequestWrapper extends ApplicationHttpRequest {

  public RequestWrapper(HttpServletRequest request) {
    super(
      (ApplicationHttpRequest) request,
      ((ApplicationHttpRequest) request).context,
      false
    );
  }

  @Override
  public String getRequestURI() {
    return Objects.toString(requestDispatcherPath, requestURI);
  }

  @Override
  public String getServletPath() {
    return Objects.toString(requestDispatcherPath, servletPath);
  }
}

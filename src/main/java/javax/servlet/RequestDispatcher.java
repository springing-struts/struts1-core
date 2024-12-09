package javax.servlet;

import jakarta.servlet.ServletException;

import java.io.IOException;

public interface RequestDispatcher extends jakarta.servlet.RequestDispatcher {
  void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException;
  void include(ServletRequest request, ServletResponse response) throws ServletException, IOException;
  static RequestDispatcher wrap(jakarta.servlet.RequestDispatcher orig) {
    return new RequestDispatcher() {
      @Override
      public void forward(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) throws ServletException, IOException {
        orig.forward(request, response);
      }

      @Override
      public void include(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) throws ServletException, IOException {
        orig.include(request, response);
      }

      @Override
      public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        orig.forward(request.unwrap(), response.unwrap());
      }

      @Override
      public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        orig.include(request.unwrap(), response.unwrap());
      }
    };
  }
}

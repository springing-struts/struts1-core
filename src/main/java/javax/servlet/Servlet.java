package javax.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Servlet {
  void init(ServletConfig config) throws ServletException;
  void destroy();
  ServletConfig getServletConfig();
  void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;
  String getServletInfo();

  static jakarta.servlet.Servlet wrap(Servlet orig) {
    return new jakarta.servlet.Servlet() {
      @Override
      public void init(jakarta.servlet.ServletConfig config) throws ServletException {
        orig.init(ServletConfig.toJavaxNamespace(config));
      }

      @Override
      public jakarta.servlet.ServletConfig getServletConfig() {
        return orig.getServletConfig();
      }

      @Override
      public void service(
        jakarta.servlet.ServletRequest req,
        jakarta.servlet.ServletResponse res
      ) throws ServletException, IOException {
        orig.service(
          HttpServletRequest.toJavaxNamespace(req),
          HttpServletResponse.toJavaxNamespace(res)
        );
      }

      @Override
      public String getServletInfo() {
        return orig.getServletInfo();
      }

      @Override
      public void destroy() {
        orig.destroy();
      }
    };
  }
}

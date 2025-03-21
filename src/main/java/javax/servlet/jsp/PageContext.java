package javax.servlet.jsp;

import jakarta.el.ELContext;
import jakarta.servlet.jsp.el.ExpressionEvaluator;
import jakarta.servlet.jsp.el.VariableResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

public abstract class PageContext extends jakarta.servlet.jsp.PageContext {

  public HttpServletRequest getHttpRequest() {
    return (HttpServletRequest) getRequest();
  }

  @Override
  public abstract ServletRequest getRequest();

  public HttpServletResponse getHttpResponse() {
    return (HttpServletResponse) getResponse();
  }

  @Override
  public abstract ServletResponse getResponse();

  @Override
  public abstract JspWriter getOut();

  @Override
  public abstract HttpSession getSession();

  @Override
  public abstract ServletContext getServletContext();

  public static PageContext toJavaxNamespace(jakarta.servlet.jsp.PageContext jakarta) {
    if (jakarta instanceof PageContext pageContext) {
      return pageContext;
    }
    return new PageContext() {
      @Override
      public void initialize(
        jakarta.servlet.Servlet servlet,
        jakarta.servlet.ServletRequest request,
        jakarta.servlet.ServletResponse response,
        String errorPageURL,
        boolean needsSession,
        int bufferSize,
        boolean autoFlush
      ) throws IOException, IllegalStateException, IllegalArgumentException {
        jakarta.initialize(
          servlet, request, response,
          errorPageURL, needsSession, bufferSize, autoFlush
        );
      }

      @Override
      public void release() {
        jakarta.release();
      }

      @Override
      public HttpSession getSession() {
        return HttpSession.toJavaxNamespace(jakarta.getSession());
      }

      @Override
      public Object getPage() {
        return jakarta.getPage();
      }

      @Override
      public HttpServletRequest getRequest() {
        return HttpServletRequest.toJavaxNamespace(jakarta.getRequest());
      }

      @Override
      public HttpServletResponse getResponse() {
        return HttpServletResponse.toJavaxNamespace(jakarta.getResponse());
      }

      @Override
      public Exception getException() {
        return jakarta.getException();
      }

      @Override
      public ServletConfig getServletConfig() {
        return ServletConfig.toJavaxNamespace(jakarta.getServletConfig());
      }

      @Override
      public ServletContext getServletContext() {
        return ServletContext.toJavaxNamespace(jakarta.getServletContext());
      }

      @Override
      public void forward(String relativeUrlPath) throws jakarta.servlet.ServletException, IOException {
        jakarta.forward(relativeUrlPath);
      }

      @Override
      public void include(String relativeUrlPath) throws jakarta.servlet.ServletException, IOException {
        jakarta.include(relativeUrlPath);
      }

      @Override
      public void include(String relativeUrlPath, boolean flush) throws jakarta.servlet.ServletException, IOException {
        jakarta.include(relativeUrlPath, flush);
      }

      @Override
      public void handlePageException(Exception e) throws jakarta.servlet.ServletException, IOException {
        jakarta.handlePageException(e);
      }

      @Override
      public void handlePageException(Throwable t) throws jakarta.servlet.ServletException, IOException {
        jakarta.handlePageException(t);
      }

      @Override
      public void setAttribute(String name, Object value) {
        jakarta.setAttribute(name, value);
      }

      @Override
      public void setAttribute(String name, Object value, int scope) {
        jakarta.setAttribute(name, value, scope);
      }

      @Override
      public Object getAttribute(String name) {
        return jakarta.getAttribute(name);
      }

      @Override
      public Object getAttribute(String name, int scope) {
        return jakarta.getAttribute(name, scope);
      }

      @Override
      public Object findAttribute(String name) {
        return jakarta.findAttribute(name);
      }

      @Override
      public void removeAttribute(String name) {
        jakarta.removeAttribute(name);
      }

      @Override
      public void removeAttribute(String name, int scope) {
        jakarta.removeAttribute(name, scope);
      }

      @Override
      public int getAttributesScope(String name) {
        return jakarta.getAttributesScope(name);
      }

      @Override
      public Enumeration<String> getAttributeNamesInScope(int scope) {
        return jakarta.getAttributeNamesInScope(scope);
      }

      @Override
      public JspWriter getOut() {
        return JspWriter.toJavaxNamespace(jakarta.getOut());
      }

      @Override
      public ExpressionEvaluator getExpressionEvaluator() {
        return jakarta.getExpressionEvaluator();
      }

      @Override
      public ELContext getELContext() {
        return jakarta.getELContext();
      }

      @Override
      public VariableResolver getVariableResolver() {
        return jakarta.getVariableResolver();
      }
    };
  }
}

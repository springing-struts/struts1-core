package javax.servlet.http;

public interface HttpServletRequest extends jakarta.servlet.http.HttpServletRequest {

  @Override
  HttpSession getSession();

  @Override
  HttpSession getSession(boolean create);
}

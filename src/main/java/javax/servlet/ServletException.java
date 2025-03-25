package javax.servlet;

public class ServletException extends jakarta.servlet.ServletException {

  public ServletException() {}

  public ServletException(String message) {
    super(message);
  }

  public ServletException(String message, Throwable rootCause) {
    super(message, rootCause);
  }

  public ServletException(Throwable rootCause) {
    super(rootCause);
  }
}

package javax.servlet.jsp;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JspException extends jakarta.servlet.jsp.JspException {
  public JspException() {
    super();
  }

  public JspException(String message, Throwable cause) {
    super(message, cause);
  }

  public JspException(Throwable cause) {
    super(cause);
  }

  public JspException(String msg) {
    super(msg);
  }

  public static JspException toJavaxNamespace(jakarta.servlet.jsp.JspException jakarta) {
    return new JspException() {
      @Override
      public StackTraceElement[] getStackTrace() {
        return jakarta.getStackTrace();
      }
      @Override
      public String getLocalizedMessage() {
        return jakarta.getLocalizedMessage();
      }
      @Override
      public String getMessage() {
        return jakarta.getMessage();
      }
      @Override
      public String toString() {
        return jakarta.toString();
      }
      @Override
      public synchronized Throwable fillInStackTrace() {
        return jakarta.fillInStackTrace();
      }
      @Override
      public synchronized Throwable getCause() {
        return jakarta.getCause();
      }
      @Override
      public synchronized Throwable initCause(Throwable cause) {
        return jakarta.initCause(cause);
      }
      @Override
      public void printStackTrace() {
        jakarta.printStackTrace();
      }
      @Override
      public void printStackTrace(PrintStream s) {
        jakarta.printStackTrace(s);
      }
      @Override
      public void printStackTrace(PrintWriter s) {
        jakarta.printStackTrace(s);
      }
      @Override
      public void setStackTrace(StackTraceElement[] stackTrace) {
        jakarta.setStackTrace(stackTrace);
      }
      @Override
      public Throwable getRootCause() {
        return jakarta.getRootCause();
      }
    };
  }
}

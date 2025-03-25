package javax.servlet;

import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;

public interface ServletResponse extends jakarta.servlet.ServletResponse {
  default jakarta.servlet.ServletResponse unwrap() {
    throw new UnsupportedOperationException();
  }

  class Wrapper implements ServletResponse {

    public Wrapper(jakarta.servlet.ServletResponse response) {
      orig = response;
    }

    private final jakarta.servlet.ServletResponse orig;

    @Override
    public jakarta.servlet.ServletResponse unwrap() {
      return orig;
    }

    @Override
    public String getCharacterEncoding() {
      return orig.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
      return orig.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      return orig.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      return orig.getWriter();
    }

    @Override
    public void setCharacterEncoding(String s) {
      orig.setCharacterEncoding(s);
    }

    @Override
    public void setContentLength(int i) {
      orig.setContentLength(i);
    }

    @Override
    public void setContentLengthLong(long l) {
      orig.setContentLengthLong(l);
    }

    @Override
    public void setContentType(String s) {
      orig.setContentType(s);
    }

    @Override
    public void setBufferSize(int i) {
      orig.setBufferSize(i);
    }

    @Override
    public int getBufferSize() {
      return orig.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
      orig.flushBuffer();
    }

    @Override
    public void resetBuffer() {
      orig.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
      return orig.isCommitted();
    }

    @Override
    public void reset() {
      orig.reset();
    }

    @Override
    public void setLocale(Locale locale) {
      orig.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
      return orig.getLocale();
    }
  }
}

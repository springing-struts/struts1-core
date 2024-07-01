package javax.servlet.http;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public interface HttpServletResponse extends jakarta.servlet.http.HttpServletResponse {
  static HttpServletResponse wrap(jakarta.servlet.http.HttpServletResponse orig) {
    return new HttpServletResponse() {
      @Override
      public void addCookie(Cookie cookie) {
        orig.addCookie(cookie);
      }

      @Override
      public boolean containsHeader(String s) {
        return orig.containsHeader(s);
      }

      @Override
      public String encodeURL(String s) {
        return orig.encodeURL(s);
      }

      @Override
      public String encodeRedirectURL(String s) {
        return orig.encodeRedirectURL(s);
      }

      @Override
      public void sendError(int i, String s) throws IOException {
        orig.sendError(i, s);
      }

      @Override
      public void sendError(int i) throws IOException {
        orig.sendError(i);
      }

      @Override
      public void sendRedirect(String s) throws IOException {
        orig.sendRedirect(s);
      }

      @Override
      public void setDateHeader(String s, long l) {
        orig.setDateHeader(s, l);
      }

      @Override
      public void addDateHeader(String s, long l) {
        orig.addDateHeader(s, l);
      }

      @Override
      public void setHeader(String s, String s1) {
        orig.setHeader(s, s1);
      }

      @Override
      public void addHeader(String s, String s1) {
        orig.addHeader(s, s1);
      }

      @Override
      public void setIntHeader(String s, int i) {
        orig.setDateHeader(s, i);
      }

      @Override
      public void addIntHeader(String s, int i) {
        orig.addIntHeader(s, i);
      }

      @Override
      public void setStatus(int i) {
        orig.setStatus(i);
      }

      @Override
      public int getStatus() {
        return orig.getStatus();
      }

      @Override
      public String getHeader(String s) {
        return orig.getHeader(s);
      }

      @Override
      public Collection<String> getHeaders(String s) {
        return orig.getHeaders(s);
      }

      @Override
      public Collection<String> getHeaderNames() {
        return orig.getHeaderNames();
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
    };
  }
}

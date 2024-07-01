package javax.servlet.http;

import jakarta.servlet.ServletContext;

import java.util.Enumeration;

public interface HttpSession extends jakarta.servlet.http.HttpSession {
  static HttpSession wrap(jakarta.servlet.http.HttpSession orig) {
    return new HttpSession() {
      @Override
      public long getCreationTime() {
        return orig.getCreationTime();
      }

      @Override
      public String getId() {
        return orig.getId();
      }

      @Override
      public long getLastAccessedTime() {
        return orig.getLastAccessedTime();
      }

      @Override
      public ServletContext getServletContext() {
        return orig.getServletContext();
      }

      @Override
      public void setMaxInactiveInterval(int interval) {
        orig.setMaxInactiveInterval(interval);
      }

      @Override
      public int getMaxInactiveInterval() {
        return orig.getMaxInactiveInterval();
      }

      @Override
      public Object getAttribute(String name) {
        return orig.getAttribute(name);
      }

      @Override
      public Enumeration<String> getAttributeNames() {
        return orig.getAttributeNames();
      }

      @Override
      public void setAttribute(String name, Object value) {
        orig.setAttribute(name, value);
      }

      @Override
      public void removeAttribute(String name) {
        orig.removeAttribute(name);
      }

      @Override
      public void invalidate() {
        orig.invalidate();
      }

      @Override
      public boolean isNew() {
        return orig.isNew();
      }
    };
  }
}

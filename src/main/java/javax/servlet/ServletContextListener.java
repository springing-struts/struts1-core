package javax.servlet;

import java.util.EventListener;

public interface ServletContextListener extends EventListener {
  default void contextInitialized(ServletContextEvent event) {
  }

  default void contextDestroyed(ServletContextEvent event) {
  }

  static jakarta.servlet.ServletContextListener wrap(ServletContextListener orig) {
    return new jakarta.servlet.ServletContextListener() {
      @Override
      public void contextInitialized(jakarta.servlet.ServletContextEvent event) {
        orig.contextInitialized(ServletContextEvent.wrap(event));
      }

      @Override
      public void contextDestroyed(jakarta.servlet.ServletContextEvent event) {
        orig.contextDestroyed(ServletContextEvent.wrap(event));
      }
    };
  }
}

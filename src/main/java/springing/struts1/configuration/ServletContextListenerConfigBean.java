package springing.struts1.configuration;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.ServletContextListener;

import static springing.util.ObjectUtils.createInstanceOf;

public class ServletContextListenerConfigBean {
  ServletContextListenerConfigBean(
    @JacksonXmlProperty(localName = "listener-class") String className
  ) {
    listenerClassName = className.trim();
  }
  private final String listenerClassName;

  public ServletContextListener createInstance() {
    var listenerInstance = createInstanceOf(listenerClassName);
    if (listenerInstance instanceof ServletContextListener jakartaListener) {
      return jakartaListener;
    }
    if (listenerInstance instanceof javax.servlet.ServletContextListener javaxListener) {
      return javax.servlet.ServletContextListener.wrap(javaxListener);
    }
    throw new IllegalStateException(String.format(
      "The instance of ths class [%s] can not be cast to [%s].",
      listenerClassName, ServletContextListener.class.getName()
    ));
  }
}
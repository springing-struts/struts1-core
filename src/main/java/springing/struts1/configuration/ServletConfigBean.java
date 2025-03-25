package springing.struts1.configuration;

import static springing.util.ObjectUtils.classFor;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.Servlet;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.struts.action.ActionServlet;
import org.springframework.lang.Nullable;
import springing.util.ObjectUtils;

public class ServletConfigBean implements ServletConfig {

  public ServletConfigBean(
    @JacksonXmlProperty(
      localName = "servlet-name",
      isAttribute = true
    ) String name,
    @JacksonXmlProperty(
      localName = "servlet-class",
      isAttribute = true
    ) String servletClassName
  ) {
    this.name = name;
    this.servletClassName = servletClassName.trim();
  }

  public Servlet createInstance() {
    var servlet = ObjectUtils.createInstanceOf(servletClassName);
    if (servlet instanceof Servlet jakartaServlet) {
      return jakartaServlet;
    }
    if (servlet instanceof javax.servlet.Servlet javaxServlet) {
      return javax.servlet.Servlet.wrap(javaxServlet);
    }
    throw new IllegalStateException(
      String.format(
        "The class [%s] of the servlet [%s] is not a Servlet class.",
        servletClassName,
        name
      )
    );
  }

  public boolean isActionServlet() {
    return ActionServlet.class.isAssignableFrom(getServletClass());
  }

  @Override
  public String getServletName() {
    return name;
  }

  private final String name;

  public Class<?> getServletClass() {
    return classFor(servletClassName);
  }

  private final String servletClassName;

  @JacksonXmlProperty(localName = "load-on-startup")
  private @Nullable Integer loadOnStartup;

  public @Nullable Integer getLoadOnStartup() {
    return loadOnStartup;
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "init-param")
  private void setInitParams(List<InitParam> params) {
    params.forEach(it -> {
      initParams.put(it.name, it.value);
    });
  }

  public Map<String, String> getInitParams() {
    return initParams;
  }

  @Override
  public @Nullable String getInitParameter(String name) {
    return initParams.get(name);
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(initParams.values());
  }

  private final Map<String, String> initParams = new HashMap<>();

  @Override
  public ServletContext getServletContext() {
    if (servletContext == null) throw new IllegalStateException(
      "This servlet is not initialized yet."
    );
    return servletContext;
  }

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  private @Nullable ServletContext servletContext;

  private static class InitParam {

    @JacksonXmlProperty(localName = "param-name")
    private String name;

    @JacksonXmlProperty(localName = "param-value")
    private String value;
  }

  public @Nullable String getUrlPattern() {
    return urlPattern;
  }

  public void setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
  }

  private @Nullable String urlPattern;
}

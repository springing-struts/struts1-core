package springing.struts1.configuration;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import org.springframework.lang.Nullable;
import springing.util.ObjectUtils;

import javax.servlet.ServletConfig;
import java.util.*;

public class ServletConfigBean implements ServletConfig {
  public ServletConfigBean(
    @JacksonXmlProperty(localName = "servlet-name", isAttribute = true) String name,
    @JacksonXmlProperty(localName = "servlet-class", isAttribute = true) String servletClass
  ) {
    this.name = name;
    this.servletClass = ObjectUtils.classFor(servletClass);
  }
  @Override
  public String getServletName() {
    return name;
  }
  private final String name;

  public Class<? extends Servlet> getServletClass() {
    return servletClass;
  }
  private final Class<? extends Servlet> servletClass;

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
  public @Nullable ServletContext getServletContext() {
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

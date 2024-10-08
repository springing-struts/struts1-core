package springing.struts1.configuration;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.struts.action.ActionServlet;
import org.springframework.lang.Nullable;

import java.util.*;

import static springing.util.ObjectUtils.parseConfigFileAt;

@JacksonXmlRootElement(localName = "web-app")
public class WebXml {

  public static WebXml loadFrom(String classPath) {
    return parseConfigFileAt(classPath, WebXml.class);
  }

  public ActionServlet getActionServlet()  {
    if (actionServletConfig == null) throw new IllegalStateException(
      "Failed to load the ActionServlet configuration from the web.xml file."
    );
    return new ActionServlet(actionServletConfig);
  }

  @JacksonXmlProperty(localName = "display-name")
  private @Nullable String displayName;

  @JacksonXmlProperty(localName = "servlet")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setServletConfigs(List<ServletConfigBean> servlets) {
    servlets.forEach(it -> {
      if (it.getServletClass().isAssignableFrom(ActionServlet.class)) {
        actionServletConfig = it;
      }
      else {
        servletConfigs.put(it.getServletName(), it);
      }
    });
  }
  public Map<String, ServletConfigBean> getServletConfigs() {
    servletMappings.forEach(mapping -> {
      servletConfigs.computeIfPresent(mapping.name, (k, config) -> {
        config.setUrlPattern(mapping.urlPattern);
        return config;
      });
    });
    return servletConfigs;
  }
  private final Map<String, ServletConfigBean> servletConfigs = new HashMap<>();
  private @Nullable ServletConfigBean actionServletConfig;

  @JacksonXmlProperty(localName = "listener")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<ServletContextListenerElement> listeners = new ArrayList<>();

  private static class ServletContextListenerElement {
    ServletContextListenerElement(
      @JacksonXmlProperty(localName = "listener-class") String className
    ) {
      this.className = className;
    }
    private final String className;
  }

  @JacksonXmlProperty(localName = "servlet-mapping")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<ServletMappingElement> servletMappings = new ArrayList<>();

  private static class ServletMappingElement {
    @JacksonXmlProperty(localName = "servlet-name")
    private String name;

    @JacksonXmlProperty(localName = "url-pattern")
    private String urlPattern;
  }

  @JacksonXmlElementWrapper(localName = "welcome-file-list")
  @JacksonXmlProperty(localName = "welcome-file")
  private final List<String> welcomeFileList = new ArrayList<>();


  @JacksonXmlProperty(localName = "taglib")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<TaglibElement> taglibs = new ArrayList<>();

  private static class TaglibElement {
    TaglibElement(
      @JacksonXmlProperty(localName = "taglib-uri", isAttribute = true) String uri,
      @JacksonXmlProperty(localName = "taglib-location", isAttribute = true) String location
    ) {
      this.uri = uri;
      this.location = location;
    }
    private final String uri;
    private final String location;
  }
}

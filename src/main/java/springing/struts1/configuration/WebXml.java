package springing.struts1.configuration;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springing.util.ObjectUtils.parseConfigFileAt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfigBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@JacksonXmlRootElement(localName = "web-app")
public class WebXml {

  public static WebXml loadFrom(String classPath) {
    return parseConfigFileAt(classPath, WebXml.class);
  }

  public ActionServlet getActionServlet() {
    if (actionServlet != null) {
      return actionServlet;
    }
    var mayActionServletConfig = getServletConfigs()
      .values()
      .stream()
      .filter(ServletConfigBean::isActionServlet)
      .findAny();
    if (mayActionServletConfig.isEmpty()) throw new IllegalStateException(
      "Failed to load the ActionServlet configuration from the web.xml file."
    );
    actionServlet = new ActionServlet(mayActionServletConfig.get());
    return actionServlet;
  }

  private @Nullable ActionServlet actionServlet;

  @JacksonXmlProperty(localName = "display-name")
  private @Nullable String displayName;

  @JacksonXmlProperty(localName = "servlet")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setServletConfigs(List<ServletConfigBean> servletConfigs) {
    this.servletConfigs = servletConfigs;
  }

  public Map<String, ServletConfigBean> getServletConfigs() {
    if (servletConfigBeanMap != null) {
      return servletConfigBeanMap;
    }
    if (
      servletConfigs == null || servletMappings == null
    ) throw new IllegalStateException(
      "The web application has not been initialized yet."
    );
    servletConfigBeanMap = new HashMap<String, ServletConfigBean>();
    for (var servletConfig : servletConfigs) {
      servletConfigBeanMap.put(servletConfig.getServletName(), servletConfig);
    }
    for (var servletMapping : servletMappings) {
      var servletConfig = servletConfigBeanMap.get(servletMapping.name);
      if (servletConfig == null) throw new IllegalStateException(
        format(
          "Failed to set servlet mapping [%s] as servlet config with name [%s] does not exists.",
          servletMapping.urlPattern,
          servletMapping.name
        )
      );
      servletConfig.setUrlPattern(servletMapping.urlPattern);
    }
    return servletConfigBeanMap;
  }

  private @Nullable Map<String, ServletConfigBean> servletConfigBeanMap;
  private @Nullable List<ServletConfigBean> servletConfigs;

  public List<
    ServletContextListenerConfigBean
  > getServletContextListenerConfigBeans() {
    return listeners;
  }

  @JacksonXmlProperty(localName = "listener")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<ServletContextListenerConfigBean> listeners =
    new ArrayList<>();

  @JacksonXmlProperty(localName = "servlet-mapping")
  @JacksonXmlElementWrapper(useWrapping = false)
  private void setServletMappings(List<ServletMappingElement> servletMappings) {
    this.servletMappings = servletMappings;
  }

  private @Nullable List<ServletMappingElement> servletMappings;

  private static class ServletMappingElement {

    @JacksonXmlProperty(localName = "servlet-name")
    private String name;

    @JacksonXmlProperty(localName = "url-pattern")
    private String urlPattern;
  }

  @JacksonXmlElementWrapper(localName = "welcome-file-list")
  @JacksonXmlProperty(localName = "welcome-file")
  private final List<String> welcomeFileList = new ArrayList<>();

  public void registerWelcomeFile(
    RequestMappingHandlerMapping mappings,
    ModuleConfigBean moduleConfig
  ) {
    final String welcomeFilePath = findEffectiveWelcomeFilePathFor(
      moduleConfig
    );
    if (welcomeFilePath == null) {
      return;
    }
    var moduleRootPath = moduleConfig.appendPrefix("/");
    mappings.registerMapping(
      RequestMappingInfo.paths(moduleRootPath)
        .methods(GET)
        .produces("text/html", "application/xhtml+xml", "application/xml")
        .build(),
      (Supplier<String>) () -> welcomeFilePath,
      SUPPLIER_GET
    );
  }

  private @Nullable String findEffectiveWelcomeFilePathFor(
    ModuleConfigBean moduleConfig
  ) {
    if (welcomeFileList.isEmpty()) {
      return null;
    }
    for (var p : welcomeFileList) {
      var path = moduleConfig.appendPrefix(p);
      if (new ClassPathResource(path).exists()) {
        return path;
      }
    }
    return null;
  }

  private static final Method SUPPLIER_GET;

  static {
    try {
      SUPPLIER_GET = Supplier.class.getMethod("get");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "error-page")
  private List<ErrorPageConfig> errorPageConfigs = new ArrayList<>();

  private static class ErrorPageConfig {

    @JacksonXmlProperty(localName = "exception-type")
    private @Nullable String exceptionType;

    @JacksonXmlProperty(localName = "error-code")
    private @Nullable Integer errorCode;

    @JacksonXmlProperty(localName = "location")
    private @Nullable String location;
  }

  @JacksonXmlProperty(localName = "taglib")
  @JacksonXmlElementWrapper(useWrapping = false)
  private final List<TaglibElement> taglibs = new ArrayList<>();

  private static class TaglibElement {

    TaglibElement(
      @JacksonXmlProperty(
        localName = "taglib-uri",
        isAttribute = true
      ) String uri,
      @JacksonXmlProperty(
        localName = "taglib-location",
        isAttribute = true
      ) String location
    ) {
      this.uri = uri;
      this.location = location;
    }

    private final String uri;
    private final String location;
  }
}

package springing.struts1.configuration;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.apache.struts.config.ModuleConfig;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;

import static springing.util.ObjectUtils.parseConfigFileAt;

@JacksonXmlRootElement(localName = "web-app")
public class WebXml {

  public static WebXml loadFrom(String classPath) {
    return parseConfigFileAt(classPath, WebXml.class);
  }

  public List<ModuleConfig> loadStrutsConfigs() {
    var results = new HashMap<String, ModuleConfig>();
    for (var param : servlet.getInitParams()) {
      var paramName = param.getName();
      var paramValue = param.getValue();
      var moduleName =
        "config".equals(paramName) ? "" :
        paramName.startsWith("config/") ? paramName.replaceFirst("^config/", "") :
        null;
      if (moduleName == null) {
        continue;
      }
      var paths = paramValue.split(",");
      for (var path : paths) {
        var config = ModuleConfig.loadFrom(path.trim(), moduleName);
        var existingConfig = results.get(moduleName);
        if (existingConfig != null) {
          existingConfig.merge(config);
        }
        results.put(moduleName, existingConfig != null ? existingConfig : config);
      }
    }
    return results.values().stream().toList();
  }

  @JacksonXmlProperty(localName = "display-name")
  private @Nullable String displayName;


  @JacksonXmlProperty(localName = "servlet")
  private ServletElement servlet;

  private static class ServletElement {
    @JacksonXmlProperty(localName = "servlet-name")
    private String name;

    @JacksonXmlProperty(localName = "servlet-class")
    private String classname;

    @JacksonXmlProperty(localName = "load-on-startup")
    private @Nullable Integer loadOnStartup;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "init-param")
    private List<InitParam> initParams;
    public List<InitParam> getInitParams() {
      return initParams;
    }
  }

  private static class InitParam {
    @JacksonXmlProperty(localName = "param-name")
    private String name;
    public String getName() {
      return name;
    }

    @JacksonXmlProperty(localName = "param-value")
    private String value;
    public String getValue() {
      return value;
    }
  }

  @JacksonXmlProperty(localName = "servlet-mapping")
  private @Nullable ServletMappingElement servletMapping;

  private static class ServletMappingElement {
    @JacksonXmlProperty(localName = "servlet-name")
    private String name;

    @JacksonXmlProperty(localName = "url-pattern")
    private String urlPattern;
  }

  @JacksonXmlElementWrapper(localName = "welcome-file-list")
  @JacksonXmlProperty(localName = "welcome-file")
  private List<String> welcomeFileList;
}

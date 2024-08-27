package springing.struts1.configuration;

import org.apache.struts.Globals;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.apache.struts.config.ModuleConfig;
import springing.struts1.controller.StrutsActionController;
import springing.util.ServletRequestUtils;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static springing.struts1.controller.StrutsActionController.DEFAULT_METHOD;

@Configuration
public class Struts1Configuration {
  public static final String STRUTS_ACTION_FORWARD_PATTERN = "/**/*.do";


  @Bean
  public ModuleUtils moduleUtils(List<ModuleConfig> moduleConfigs,
                                 jakarta.servlet.http.HttpServletRequest request
  ) {
    ServletRequestUtils.initialize(request);
    ModuleUtils.initialize(moduleConfigs);
    return ModuleUtils.getInstance();
  }

  @Bean
  public List<ModuleConfig> moduleConfigs() {
    var webXml = WebXml.loadFrom("/WEB-INF/web.xml");
    return webXml.loadStrutsConfigs();
  }

  @Primary
  @Bean
  public RequestMappingHandlerMapping strutsRequestMappingHandlerMapping(
    List<ModuleConfig> moduleConfigs,
    RequestMappingHandlerMapping mappings
  ) {
    for (var moduleConfig : moduleConfigs) {
      for (var actionConfig : moduleConfig.getActionConfigs()) {
        var actionUrl = actionConfig.getActionUrl();
        var mapping = RequestMappingInfo
            .paths(actionUrl, actionUrl + ".do")
            .methods(GET, POST)
            .produces("text/html", "application/xhtml+xml", "application/xml", "text/plain")
            .build();
        mappings.registerMapping(
            mapping,
            new StrutsActionController(actionConfig),
            DEFAULT_METHOD
        );
      }
    }
    return mappings;
  }


}

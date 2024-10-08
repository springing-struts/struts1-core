package springing.struts1.configuration;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.apache.struts.config.ModuleConfig;
import springing.struts1.controller.StrutsActionController;
import springing.struts1.controller.RequestTokenManager;
import springing.util.ServletRequestUtils;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static springing.struts1.controller.StrutsActionController.DEFAULT_METHOD;

@Configuration
public class Struts1Configuration {

  public static final String STRUTS_ACTION_FORWARD_PATTERN = "/**/*.do";

  @Bean
  public ModuleUtils moduleUtils(
    List<ModuleConfigBean> moduleConfigs
  ) {
    ModuleUtils.initialize(moduleConfigs);
    return ModuleUtils.getInstance();
  }

  @Bean
  public ServletRequestUtils servletRequestUtils(
    jakarta.servlet.http.HttpServletRequest request,
    ConversionService conversionService
  ) {
    return new ServletRequestUtils(request, conversionService);
  }

  @Bean
  public List<ModuleConfigBean> moduleConfigs(WebXml webXml) {
    return webXml.getActionServlet().getStrutsModules();
  }

  @Bean
  public ActionServlet actionServlet(WebXml webXml) {
    return webXml.getActionServlet();
  }

  @Bean
  public Map<String, ServletConfigBean> servletConfigs(WebXml webXml) {
    return webXml.getServletConfigs();
  }

  @Bean
  public WebXml webXml() {
    return WebXml.loadFrom("/WEB-INF/web.xml");
  }

  @Bean
  @SessionScope
  public RequestTokenManager requestTokenManager() {
    return new RequestTokenManager();
  }

  @Primary
  @Bean
  public RequestMappingHandlerMapping strutsRequestMappingHandlerMapping(
    GenericApplicationContext context,
    List<ModuleConfigBean> moduleConfigs,
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
            new StrutsActionController(actionConfig, context),
            DEFAULT_METHOD
        );
      }
    }
    return mappings;
  }
}

package springing.struts1.configuration;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springing.struts1.controller.RequestTokenManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Configuration
public class StrutsConfiguration {

  public static final String STRUTS_ACTION_FORWARD_PATTERN = "/**/*.do";

  @Bean
  public ModuleUtils moduleUtils(jakarta.servlet.http.HttpServletRequest request) {
    ModuleUtils.initialize(
      actionServlet().getStrutsModules(),
      HttpServletRequest.toJavaxNamespace(request)
    );
    return ModuleUtils.getInstance();
  }

  @Bean
  public ActionServlet actionServlet() {
    return webXml().getActionServlet();
  }

  @Bean
  @RequestScope
  public ModuleConfigBean moduleConfig(ModuleUtils moduleUtils) {
    return moduleUtils.getModuleConfig();
  }

  /**
   * The default message resource associated with the current module.
   */
  @Bean
  @RequestScope
  public MessageResources messageResources(ModuleConfigBean moduleConfig) {
    return moduleConfig.getMessageResources();
  }

  @Bean
  @RequestScope
  public RequestProcessor requestProcessor(
    ModuleConfigBean moduleConfig,
    ServletActionContext actionContext
  ) throws ServletException {
    var requestProcessor = moduleConfig.getRequestProcessor();
    requestProcessor.init(actionServlet(), moduleConfig);
    return requestProcessor;
  }

  @Bean
  @RequestScope
  public ServletActionContext servletActionContext(
    ConversionService conversionService
  ) {
    return new ServletActionContext(
      actionServlet(),
      conversionService
    );
  }

  @Bean
  public Map<String, ServletConfigBean> servletConfigs(WebXml webXml) {
    return webXml.getServletConfigs();
  }

  @Bean
  public List<ServletContextListenerConfigBean> contextListenerConfigs(WebXml webXml) {
    return webXml.getServletContextListenerConfigBeans();
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
    RequestMappingHandlerMapping mappings,
    ServletActionContext actionContext,
    GenericApplicationContext context,
    RequestProcessor requestProcessor
  ) throws Exception {
    webXml().registerWelcomeFile(mappings);
    for (var moduleConfig : actionServlet().getStrutsModules()) {
      for (var actionConfig : moduleConfig.getActionConfigs()) {
        actionConfig.registerRequestMapping(
          mappings, actionContext, requestProcessor, context
        );
      }
    }
    return mappings;
  }
}

package springing.struts1.configuration;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import springing.struts1.controller.JspForwardingHandler;
import springing.struts1.controller.RequestTokenManager;

@Configuration
public class StrutsConfiguration {

  public static final String STRUTS_ACTION_FORWARD_PATTERN = "/**/*.do";

  @Bean
  public ModuleUtils moduleUtils(
    jakarta.servlet.http.HttpServletRequest request
  ) {
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

  @Bean
  @RequestScope
  public TagUtils tagUtils() {
    return new TagUtils();
  }

  @Bean
  public TagUtils.Holder tagUtilsHolder(TagUtils instance) {
    return new TagUtils.Holder(instance);
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
  public ServletActionContext actionContext(
    ModuleConfigBean moduleConfig,
    ConversionService conversionService
  ) {
    return new ServletActionContext(
      actionServlet(),
      moduleConfig,
      conversionService
    );
  }

  @Bean
  public ServletActionContext.Holder actionContextHolder(
    ServletActionContext instance
  ) {
    return new ServletActionContext.Holder(instance);
  }

  @Bean
  public Map<String, ServletConfigBean> servletConfigs(WebXml webXml) {
    return webXml.getServletConfigs();
  }

  @Bean
  public List<ServletContextListenerConfigBean> contextListenerConfigs(
    WebXml webXml
  ) {
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

  @Bean
  public ResourceHttpRequestHandler staticResourceHandler() {
    var bean = new ResourceHttpRequestHandler();
    bean.setLocationValues(List.of("/"));
    return bean;
  }

  @Bean
  public JspForwardingHandler jspResourceHandler(
    ResourceHttpRequestHandler staticResourceHandler
  ) {
    return new JspForwardingHandler(staticResourceHandler);
  }

  @Primary
  @Bean
  public RequestMappingHandlerMapping strutsRequestMappingHandlerMapping(
    RequestMappingHandlerMapping mappings,
    ServletActionContext actionContext,
    GenericApplicationContext context,
    RequestProcessor requestProcessor,
    JspForwardingHandler jspResourceHandler
  ) throws Exception {
    var webxml = webXml();
    for (var moduleConfig : actionServlet().getStrutsModules()) {
      webxml.registerWelcomeFile(mappings, moduleConfig);
      for (var actionConfig : moduleConfig.getActionConfigs()) {
        actionConfig.registerRequestMapping(
          mappings,
          actionContext,
          requestProcessor,
          jspResourceHandler,
          context
        );
      }
    }
    return mappings;
  }
}

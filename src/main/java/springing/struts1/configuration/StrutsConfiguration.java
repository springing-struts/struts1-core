package springing.struts1.configuration;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springing.struts1.controller.RequestTokenManager;
import springing.util.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Configuration
public class StrutsConfiguration {

  public static final String STRUTS_ACTION_FORWARD_PATTERN = "/**/*.do";

  @Bean
  public ModuleUtils moduleUtils() {
    ModuleUtils.initialize(actionServlet().getStrutsModules());
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
  public ActionServlet actionServlet() {
    return webXml().getActionServlet();
  }

  @Bean
  @RequestScope
  public ModuleConfigBean moduleConfig() {
    return ModuleUtils.getCurrent();
  }

  /**
   * The default message resource associated with the current module.
   */
  @Bean
  @RequestScope
  public MessageResources messageResources(
    HttpServletRequest request
  ) {
    return ModuleUtils.getCurrent().getMessageResources();
  }

  @Bean
  @RequestScope
  public RequestProcessor requestProcessor(
    HttpServletRequest request
  ) throws ServletException {
    var moduleConfig =  moduleConfig();
    request.setAttribute(Globals.MESSAGES_KEY, moduleConfig.getMessageResources());
    var requestProcessor = moduleConfig.getRequestProcessor();
    requestProcessor.init(actionServlet(), moduleConfig());
    return requestProcessor;
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
    GenericApplicationContext context,
    RequestProcessor requestProcessor
  ) throws Exception {
    var welcomeFilePath = webXml().getWelcomeFile();
    if (welcomeFilePath != null) {
      mappings.registerMapping(
        RequestMappingInfo
          .paths("/")
          .methods(GET)
          .produces("text/html", "application/xhtml+xml", "application/xml")
          .build(),
        (Supplier<String>) () -> welcomeFilePath,
        Supplier.class.getMethod("get")
      );
    }
    for (var moduleConfig : actionServlet().getStrutsModules()) {
      for (var actionConfig : moduleConfig.getActionConfigs()) {
        actionConfig.registerAction(context);
        actionConfig.registerMapping(mappings, requestProcessor);
      }
    }
    return mappings;
  }
}

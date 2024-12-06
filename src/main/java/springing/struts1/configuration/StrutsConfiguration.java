package springing.struts1.configuration;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
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
import springing.struts1.controller.StrutsRequestContext;
import springing.struts1.controller.RequestTokenManager;
import springing.util.ServletRequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
  ) throws ServletException {
    for (var moduleConfig : actionServlet().getStrutsModules()) {
      for (var actionConfig : moduleConfig.getActionConfigs()) {
        var actionUrl = actionConfig.getActionUrl();
        var mapping = RequestMappingInfo
            .paths(actionUrl, actionUrl + ".do")
            .methods(GET, POST)
            .produces("text/html", "application/xhtml+xml", "application/xml", "text/plain")
            .build();
        actionConfig.registerAction(context);
        if (!(actionConfig instanceof ActionMapping actionMapping)) throw new IllegalStateException(format(
          "The actionConfig [%s] should be an instance of [%s].",
          actionConfig.getName(), ActionMapping.class.getName()
        ));
        mappings.registerMapping(mapping, (StrutsRequestProcessing) (request, response) -> {
          StrutsRequestContext.setActionMapping(actionMapping);
          requestProcessor.process(
            HttpServletRequest.toJavaxNamespace(request),
            HttpServletResponse.toJavaxNamespace(response)
          );
        }, PROCESS_METHOD);
      }
    }
    return mappings;
  }

  @FunctionalInterface
  private interface StrutsRequestProcessing {
    void process(
      jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response
    ) throws ServletException, IOException;
  }
  public static final Method PROCESS_METHOD;
  static {
    try {
      PROCESS_METHOD = StrutsRequestProcessing.class.getDeclaredMethod(
        "process",
        jakarta.servlet.http.HttpServletRequest.class,
        jakarta.servlet.http.HttpServletResponse.class
      );
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}

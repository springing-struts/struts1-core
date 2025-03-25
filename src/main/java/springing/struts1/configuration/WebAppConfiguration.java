package springing.struts1.configuration;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.jasper.servlet.JasperInitializer;
import org.apache.jasper.servlet.JspServlet;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfigBean;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.apache.struts.validator.ValidatorPlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class WebAppConfiguration implements ServletContextInitializer {

  public WebAppConfiguration(
    ActionServlet actionServlet,
    Map<String, ServletConfigBean> servletConfigs,
    MessageResources messageResources,
    ModuleConfigBean moduleConfig,
    List<ServletContextListenerConfigBean> contextListenerConfigs
  ) {
    this.actionServlet = actionServlet;
    this.servletConfigs = servletConfigs;
    this.moduleConfig = moduleConfig;
    this.contextListenerConfigs = contextListenerConfigs;
  }

  private final ActionServlet actionServlet;
  private final Map<String, ServletConfigBean> servletConfigs;
  private final ModuleConfigBean moduleConfig;
  private final List<ServletContextListenerConfigBean> contextListenerConfigs;

  private static final Logger logger = LoggerFactory.getLogger(
    WebAppConfiguration.class
  );

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    initApplicationScopeVariables(servletContext);
    for (var listenerConfig : contextListenerConfigs) {
      servletContext.addListener(listenerConfig.createInstance());
    }
    registerDispatcherServlet(servletContext);
    registerJspServlet(servletContext);
    servletConfigs.forEach((name, servletConfig) -> {
      if (!servletConfig.isActionServlet()) {
        registerAdditionalServlet(servletConfig, servletContext);
      }
    });
    ModuleUtils.getInstance().loadPlugIns(actionServlet);
    for (ModuleConfigBean module : actionServlet.getStrutsModules()) {
      initApplicationScopeVariablesForModule(servletContext, module);
    }
  }

  private void initApplicationScopeVariables(ServletContext servletContext) {
    servletContext.setAttribute(Globals.ACTION_SERVLET_KEY, actionServlet);
    servletContext.setAttribute(Globals.MODULE_KEY, moduleConfig);
  }

  private void initApplicationScopeVariablesForModule(
    ServletContext servletContext,
    ModuleConfigBean module
  ) {
    var modulePrefix = module.getPrefix().equals("/") ? "" : module.getPrefix();
    servletContext.setAttribute(
      ValidatorPlugIn.VALIDATOR_KEY + modulePrefix,
      module.getValidatorResources()
    );
    servletContext.setAttribute(
      Globals.MESSAGES_KEY + modulePrefix,
      module.getMessageResources()
    );
  }

  private void registerDispatcherServlet(ServletContext servletContext)
    throws ServletException {
    var appContext = new AnnotationConfigWebApplicationContext();
    appContext.register(WebMvcConfiguration.class);
    appContext.setServletContext(servletContext);
    var dispatcherServlet = new DispatcherServlet(appContext);
    dispatcherServlet.setApplicationContext(appContext);
    var dispatcher = servletContext.addServlet(
      "dispatcherServlet",
      dispatcherServlet
    );
    dispatcher.setLoadOnStartup(actionServlet.getLoadOnStartup());
    actionServlet.setServletContext(servletContext);
    dispatcher.setMultipartConfig(new MultipartConfigElement("/"));
    dispatcher.addMapping("/", "*.jsp");
  }

  private void registerJspServlet(ServletContext servletContext)
    throws ServletException {
    var jasperInitializer = new JasperInitializer();
    jasperInitializer.onStartup(Collections.emptySet(), servletContext);

    var jsp = servletContext.addServlet("jsp", JspServlet.class.getName());
    jsp.setLoadOnStartup(10);
    jsp.setInitParameters(
      Map.of(
        "fork",
        "false",
        "development",
        "false",
        "strictGetProperty",
        "false"
      )
    );
    jsp.addMapping("*.jsp");
  }

  private void registerAdditionalServlet(
    ServletConfigBean config,
    ServletContext context
  ) {
    var registration = context.addServlet(
      config.getServletName(),
      config.createInstance()
    );
    var loadOnStartup = config.getLoadOnStartup();
    if (loadOnStartup != null) {
      registration.setLoadOnStartup(loadOnStartup);
    }
    config.getInitParams().forEach(registration::setInitParameter);
    var mapping = config.getUrlPattern();
    if (mapping != null) {
      registration.addMapping(mapping);
    }
  }
}

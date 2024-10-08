package springing.struts1.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.jasper.servlet.JasperInitializer;
import org.apache.jasper.servlet.JspServlet;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.ModuleUtils;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Collections;
import java.util.Map;

@Configuration
public class WebAppConfiguration implements ServletContextInitializer {

  public WebAppConfiguration(
    ActionServlet actionServlet,
    Map<String, ServletConfigBean> servletConfigs
  ) {
    this.actionServlet = actionServlet;
    this.servletConfigs = servletConfigs;
  }
  private final ActionServlet actionServlet;
  private final Map<String, ServletConfigBean> servletConfigs;

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    registerDispatcherServlet(servletContext);
    registerJspServlet(servletContext);
    servletConfigs.forEach((name, servletConfig) -> {
      registerAdditionalServlet(servletConfig, servletContext);
    });
    ModuleUtils.getInstance().loadPlugIns(actionServlet);
  }

  private void registerAdditionalServlet(ServletConfigBean config, ServletContext context) {
    var servlet = context.addServlet(
      config.getServletName(),
      config.getServletClass()
    );
    var loadOnStartup = config.getLoadOnStartup();
    if (loadOnStartup != null) {
      servlet.setLoadOnStartup(loadOnStartup);
    }
    config.getInitParams().forEach(servlet::setInitParameter);
    var mapping = config.getUrlPattern();
    if (mapping != null) {
      servlet.addMapping(mapping);
    }
  }

  private void registerDispatcherServlet(ServletContext servletContext) throws ServletException {
    var appContext = new AnnotationConfigWebApplicationContext();
    appContext.register(WebMvcConfiguration.class);
    appContext.setServletContext(servletContext);
    var dispatcherServlet = new DispatcherServlet(appContext);
    dispatcherServlet.setApplicationContext(appContext);
    var dispatcher = servletContext.addServlet(
      "dispatcherServlet", dispatcherServlet
    );
    dispatcher.setLoadOnStartup(actionServlet.getLoadOnStartup());
    dispatcher.addMapping("/", "*.jsp");
  }

  private void registerJspServlet(ServletContext servletContext) throws ServletException {
    var jasperInitializer =  new JasperInitializer();
    jasperInitializer.onStartup(Collections.emptySet(), servletContext);

    var jsp = servletContext.addServlet("jsp", JspServlet.class.getName());
    jsp.setLoadOnStartup(10);
    jsp.setInitParameters(Map.of(
      "fork", "false",
      "development", "false",
      "strictGetProperty", "false"
    ));
    jsp.addMapping("*.jsp");
  }
}

package springing.struts1.configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.jasper.servlet.JasperInitializer;
import org.apache.jasper.servlet.JspServlet;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Collections;
import java.util.Map;

@Configuration
public class WebAppConfiguration implements ServletContextInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {

    var appContext = new AnnotationConfigWebApplicationContext();
    appContext.register(WebMvcConfiguration.class);
    appContext.setServletContext(servletContext);

    var dispatcher = servletContext.addServlet(
      "dispatcherServlet", new DispatcherServlet(appContext)
    );

    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/", "*.jsp");

    var jasperInitializer =  new JasperInitializer();
    jasperInitializer.onStartup(Collections.emptySet(), servletContext);

    var jsp = servletContext.addServlet("jsp", JspServlet.class.getName());
    jsp.setLoadOnStartup(5);
    jsp.setInitParameters(Map.of(
      "fork", "false",
      "development", "false",
      "strictGetProperty", "false"
    ));
    jsp.addMapping("*.jsp");
  }
}

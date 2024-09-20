package springing.struts1.configuration;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springing.struts1.controller.JspForwardingHandler;
import springing.struts1.controller.StrutsViewResolutionInterceptor;
import java.util.Map;
import static springing.struts1.configuration.Struts1Configuration.STRUTS_ACTION_FORWARD_PATTERN;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


  @Bean
  public HandlerMapping defaultServletHttpRequestHandler(ServletContext servletContext) {
    var handler = new JspForwardingHandler(servletContext);
    handler.setDefaultServletName("jsp");
    handler.setServletContext(servletContext);
    return new SimpleUrlHandlerMapping(Map.of(
      "/**", handler
    ));
  }

  @Bean
  public ViewResolver jspViewResolver() {
    var bean = new InternalResourceViewResolver();
    bean.setViewClass(JstlView.class);
    return bean;
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.viewResolver(jspViewResolver());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(
      new StrutsViewResolutionInterceptor()
    ).addPathPatterns(STRUTS_ACTION_FORWARD_PATTERN);
  }
}

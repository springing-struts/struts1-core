package springing.struts1.configuration;

import static springing.struts1.configuration.StrutsConfiguration.STRUTS_ACTION_FORWARD_PATTERN;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springing.struts1.controller.JspForwardingHandler;
import springing.struts1.controller.StrutsViewResolutionInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Bean
  public HandlerMapping defaultServletHttpRequestHandler(
    JspForwardingHandler jspResourceHandler
  ) {
    return new SimpleUrlHandlerMapping(Map.of("/**", jspResourceHandler));
  }

  @Bean
  public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver();
  }

  @Bean
  public ViewResolver jspViewResolver() {
    var bean = new InternalResourceViewResolver();
    bean.setExposeContextBeansAsAttributes(true);
    bean.setViewClass(JstlView.class);
    return bean;
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.viewResolver(jspViewResolver());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
      .addInterceptor(new StrutsViewResolutionInterceptor())
      .addPathPatterns(STRUTS_ACTION_FORWARD_PATTERN);
  }
}

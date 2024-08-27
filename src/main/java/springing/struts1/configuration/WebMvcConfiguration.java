package springing.struts1.configuration;

import org.apache.struts.Globals;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springing.struts1.controller.StrutsViewResolutionInterceptor;

import java.util.Locale;

import static springing.struts1.configuration.Struts1Configuration.STRUTS_ACTION_FORWARD_PATTERN;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

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

package springing.struts1.configuration;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import springing.struts1.controller.StrutsViewResolutionInterceptor;

import java.io.IOException;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;
import static springing.struts1.configuration.Struts1Configuration.STRUTS_ACTION_FORWARD_PATTERN;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

  @Bean
  public ViewResolver jspViewResolver() {
    var bean = new InternalResourceViewResolver();
    bean.setViewClass(JstlView.class);
    return bean;
  }

  /*
  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable("jsp");
  }
   */

  @Bean
  public HandlerMapping defaultServletHttpRequestHandler(ServletContext servletContext) {
    var handler = new DefaultServletHttpRequestHandler() {
      @Override
      public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        servletContext.getContext("jsp");
        RequestDispatcher rd = servletContext.getNamedDispatcher("jsp");
        if (rd == null) {
          throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet: jsp.");
        }
        if (request.getDispatcherType() != DispatcherType.INCLUDE) {
          rd.forward(request, response);
        }
        else {
          rd.include(request, response);
        }
      }
    };
    handler.setDefaultServletName("jsp");
    handler.setServletContext(servletContext);
    return new SimpleUrlHandlerMapping(Map.of(
      "/**", handler
    ));
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

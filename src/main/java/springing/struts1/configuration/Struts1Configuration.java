package springing.struts1.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.apache.struts.config.StrutsConfig;
import springing.struts1.action.ActionController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static springing.struts1.action.ActionController.DEFAULT_METHOD;

@Configuration
public class Struts1Configuration {

  @Bean
  public StrutsConfig strutsConfig() {
    return StrutsConfig.parseConfigFileAt("/WEB-INF/struts-config.xml");
  }

  @Primary
  @Bean
  public RequestMappingHandlerMapping strutsRequestMappingHandlerMapping(
    StrutsConfig strutsConfig,
    RequestMappingHandlerMapping mappings
  ) {
    for (var actionConfig : strutsConfig.getActionConfigs()) {
      var path = actionConfig.getPath();
      var mapping = RequestMappingInfo
          .paths(path)
          .methods(GET)
          .build();
      mappings.registerMapping(
        mapping,
        new ActionController(actionConfig),
        DEFAULT_METHOD
      );
    }
    return mappings;
  }

  @Bean
  public MessageSource messageSource(
    StrutsConfig config
  ) {
    var messageResourceConfig = config.getMessageResourceConfig();
    return messageResourceConfig.toMessageSource();
  }
}

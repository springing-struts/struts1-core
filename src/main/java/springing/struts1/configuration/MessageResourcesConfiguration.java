package springing.struts1.configuration;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import springing.struts1.message.MessageResourcesWrapper;
import springing.struts1.message.StrutsModuleAwareMessageSource;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MessageResourcesConfiguration {

  @Bean
  @Primary
  public StrutsModuleAwareMessageSource messageSource(
    ModuleUtils moduleUtils,
    HttpServletRequest request
  ) {
    var bean = new StrutsModuleAwareMessageSource(moduleUtils, request);
    MESSAGE_SOURCE = bean;
    return bean;
  }

  private static @Nullable StrutsModuleAwareMessageSource MESSAGE_SOURCE;

  public static MessageResources getMessageResources(String basename) {
    if (MESSAGE_SOURCE == null) throw new IllegalStateException(
      "MessageResourcesConfiguration must be initialized before accessing MessageResources."
    );
    return new MessageResourcesWrapper(basename, MESSAGE_SOURCE.getMessageSourceFor(basename));
  }



}

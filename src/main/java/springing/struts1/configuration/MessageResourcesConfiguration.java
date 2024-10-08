package springing.struts1.configuration;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import springing.struts1.message.MessageResourcesWrapper;
import springing.struts1.message.StrutsModuleAwareMessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

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

  public static MessageResources getMessageResources(String bundleName) {
    if (MESSAGE_SOURCE == null) throw new IllegalStateException(
      "MessageResourcesConfiguration must be initialized before accessing MessageResources."
    );
    return new MessageResourcesWrapper(
      bundleName,
      MESSAGE_SOURCE.getMessageSourceForCurrentModule(bundleName)
    );
  }

  @Bean
  public LocaleResolver localeResolver() {
    var resolver = new SessionLocaleResolver();
    resolver.setDefaultLocale(Locale.US);
    resolver.setLocaleAttributeName(Globals.LOCALE_KEY);
    return resolver;
  }
}

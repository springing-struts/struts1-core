package springing.struts1.configuration;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.Globals;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import springing.struts1.message.StrutsModuleAwareMessageSource;

@Configuration
public class MessageResourcesConfiguration {

  @Bean
  @Primary
  public StrutsModuleAwareMessageSource messageSource(
    ModuleUtils moduleUtils,
    HttpServletRequest request
  ) {
    return new StrutsModuleAwareMessageSource(moduleUtils, request);
  }

  @Bean
  public LocaleResolver localeResolver() {
    var resolver = new SessionLocaleResolver();
    resolver.setDefaultLocale(Locale.US);
    resolver.setLocaleAttributeName(Globals.LOCALE_KEY);
    return resolver;
  }
}

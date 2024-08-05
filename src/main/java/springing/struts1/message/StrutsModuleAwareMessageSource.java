package springing.struts1.message;

import org.apache.struts.config.ModuleConfig;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A `MessageSource` implementation that dynamically switches the resource
 * bundle from which it retrieves messages based on the module associated with
 * the current request.
 */
public class StrutsModuleAwareMessageSource implements MessageSource {

  public StrutsModuleAwareMessageSource(
    ModuleUtils moduleUtils,
    HttpServletRequest request
  ) {
    this.moduleUtils = moduleUtils;
    this.request = request;
  }
  private final ModuleUtils moduleUtils;
  private final HttpServletRequest request;

  public MessageSource getMessageSourceFor(String basename) {
    return loadedMessageSource.computeIfAbsent(
        basename,
        (k) -> {
          var messageSource = new ResourceBundleMessageSource();
          messageSource.setBasename(basename);
          messageSource.setDefaultEncoding("UTF-8");
          return messageSource;
        });
  }
  private final ConcurrentMap<String, ResourceBundleMessageSource>
      loadedMessageSource = new ConcurrentHashMap<>();

  private MessageSource forModule(ModuleConfig module, String key) {
    var configs = module.getMessageResourcesConfigs();
    for (var config : configs) {
      if (config.getKey().equals(key)) {
        var basename = config.getConfig();
        return getMessageSourceFor(basename);
      }
    }
    throw new IllegalStateException(String.format(
      "MessageResources with key [%s] is not defined for the module [%s].",
      key, module.getPrefix()
    ));
  }

  private MessageSource getMessageSourceForCurrentModule(String key) {
    var module = moduleUtils.getModuleConfig(request);
    return forModule(module, key);
  }

  @Override
  public @Nullable String getMessage(
      String code,
      @Nullable Object[] args,
      @Nullable String defaultMessage,
      Locale locale
  ) {
    var bundleKey = (String) args[5];
    var arguments = Arrays.copyOfRange(args, 0, 4);
    return getMessageSourceForCurrentModule(bundleKey).getMessage(code, arguments, defaultMessage, locale);
  }

  @Override
  public String getMessage(
      String code,
      @Nullable Object[] args,
      Locale locale
  ) throws NoSuchMessageException {
    var bundleKey = (String) args[5];
    var arguments = Arrays.copyOfRange(args, 0, 4);
    return getMessageSourceForCurrentModule(bundleKey).getMessage(code, arguments, locale);
  }

  @Override
  public String getMessage(
      MessageSourceResolvable resolvable,
      Locale locale
  ) throws NoSuchMessageException {
    var bundleKey = (String) resolvable.getArguments()[5];
    resolvable.getArguments()[5] = null;
    return getMessageSourceForCurrentModule(bundleKey).getMessage(resolvable, locale);
  }
}


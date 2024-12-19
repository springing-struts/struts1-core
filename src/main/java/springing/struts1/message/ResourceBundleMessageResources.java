package springing.struts1.message;

import org.apache.struts.util.MessageResources;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thin wrapper that provides a Spring MessageSource as a Struts
 * MessageResources.
 */
public class ResourceBundleMessageResources extends MessageResources {

  public static ResourceBundleMessageResources load(String bundleName) {
    return loadedMessageResourcesByBundleName.computeIfAbsent(
      bundleName,
      ResourceBundleMessageResources::new
    );
  }

  private ResourceBundleMessageResources(String bundleName) {
    messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(bundleName);
    messageSource.setDefaultEncoding("UTF-8");
    this.bundleName = bundleName;
  }

  public MessageSource toMessageSource() {
    return messageSource;
  }

  private final ResourceBundleMessageSource messageSource;
  private final String bundleName;

  private static final ConcurrentMap<String, ResourceBundleMessageResources>
    loadedMessageResourcesByBundleName = new ConcurrentHashMap<>();

  /**
   * Returns a text message for the specified key, for the default Locale.
   * A null string result will be returned by this method if no relevant
   * message resource is found for this key or Locale, if the`returnNull`
   * property is set. Otherwise, an appropriate error message will be
   * returned. This method must be implemented by a concrete subclass.
   */
  public @Nullable String getMessage(
    @Nullable Locale locale, String key, Object... args
  ) {
    return messageSource.getMessage(
      key,
      args,
      null,
      locale != null ? locale : LocaleContextHolder.getLocale()
    );
  }

  /**
   * The configuration parameter used to initialize this MessageResources.
   */
  public String getConfig() {
    return bundleName;
  }
}
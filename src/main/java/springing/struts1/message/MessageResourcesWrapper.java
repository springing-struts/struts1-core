package springing.struts1.message;

import org.apache.struts.util.MessageResources;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import springing.util.ServletRequestUtils;

import java.util.Locale;

/**
 * A thin wrapper that provides a Spring MessageResource as a Struts
 * MessageResources.
 */
public class MessageResourcesWrapper implements MessageResources {

  public MessageResourcesWrapper(String basename, MessageSource source) {
    this.basename = basename;
    this.messageSource = source;
  }
  private final String basename;
  private final MessageSource messageSource;

  /**
   * Returns a text message for the specified key, for the default Locale.
   */
  public @Nullable String getMessage(String key, Object... args) {
    return getMessage(null, key, args);
  }

  /**
   * Returns a text message for the specified key, for the default Locale. A
   * null string result will be returned by this method if no relevant
   * message resource is found for this key or Locale, if the`returnNull`
   * property is set.  Otherwise, an appropriate error message will be
   * returned. This method must be implemented by a concrete subclass.
   */
  public @Nullable String getMessage(@Nullable Locale locale, String key, Object... args) {
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
    return basename;
  }
}
package springing.struts1.configuration;

import org.apache.struts.util.MessageResources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.lang.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Configuration
public class MessageResourcesConfiguration {

  public static MessageResources getMessageResources(String config) {
    return new LazyLoadingMessageResources(config);
  }

  private static final Map<String, ResourceBundleMessageSource> messageSourceByPath = new HashMap<>();

  @Bean
  public Map<String, ResourceBundleMessageSource> messageSourceByPath() throws IOException {
    var scanner = new PathMatchingResourcePatternResolver();
    var resources = scanner.getResources("classpath:/**/*.properties");
    for (var resource : resources) {
      var filename = resource.getFilename();
      if (isMessageResourceBundleName(filename)) {
        var basename = resource.getURL().getPath();
        var bundle = createResourceBundle(basename);
        messageSourceByPath.put(basename, bundle);
      }
    }
    return messageSourceByPath;
  }

  private ResourceBundleMessageSource createResourceBundle(String basename)  {
    var bundle = new ResourceBundleMessageSource();
    bundle.setBasename(basename);
    bundle.setDefaultEncoding("UTF-8");
    return bundle;
  }

  private boolean isMessageResourceBundleName(@Nullable String filename) {
    return filename != null && (filename.endsWith("Strings") || filename.endsWith("Resources"));
  }

  private static class LazyLoadingMessageResources implements MessageResources {
    public LazyLoadingMessageResources(String baseName) {
      this.baseName = baseName;
    }
    private final String baseName;
    private @Nullable ResourceBundleMessageSource messageSource;

    private ResourceBundleMessageSource getMessageSource() {
      if (messageSource == null) {
        messageSource = messageSourceByPath.get(baseName);
      }
      return messageSource;
    }

    /**
     * Returns a text message for the specified key, for the default Locale.
     */
    public String getMessage(String key, Object... args) {
      return getMessage(Locale.getDefault(), key, args);
    }

    /**
     * Returns a text message for the specified key, for the default Locale. A
     * null string result will be returned by this method if no relevant
     * message resource is found for this key or Locale, if the`returnNull`
     * property is set.  Otherwise, an appropriate error message will be
     * returned. This method must be implemented by a concrete subclass.
     */
    public String getMessage(Locale locale, String key, Object... args) {
      return getMessageSource().getMessage(key, args, locale);
    }

    /**
     * The configuration parameter used to initialize this MessageResources.
     */
    public String getConfig() {
      return baseName;
    }
  }


}

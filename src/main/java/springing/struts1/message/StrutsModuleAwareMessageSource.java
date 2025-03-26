package springing.struts1.message;

import java.util.Arrays;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.util.ModuleUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;

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

  private MessageSource getMessageSourceForCurrentModule(String key) {
    var module = moduleUtils.getModuleConfig(request);
    return module.getMessageResources(key).toMessageSource();
  }

  @Override
  public @Nullable String getMessage(
    String code,
    @Nullable Object[] args,
    @Nullable String defaultMessage,
    Locale locale
  ) {
    args = args == null ? new String[] { "", "", "", "", "" } : args;
    var bundleKey = args.length < 6 ? "" : (String) args[5];
    var arguments = Arrays.copyOfRange(args, 0, 4);
    return getMessageSourceForCurrentModule(bundleKey).getMessage(
      code,
      arguments,
      defaultMessage,
      locale
    );
  }

  @Override
  public String getMessage(String code, @Nullable Object[] args, Locale locale)
    throws NoSuchMessageException {
    args = args == null ? new String[] { "", "", "", "", "" } : args;
    var bundleKey = args.length < 6 ? "" : (String) args[5];
    var arguments = Arrays.copyOfRange(args, 0, 4);
    return getMessageSourceForCurrentModule(bundleKey).getMessage(
      code,
      arguments,
      locale
    );
  }

  @Override
  public String getMessage(MessageSourceResolvable resolvable, Locale locale)
    throws NoSuchMessageException {
    var bundleKey = (String) resolvable.getArguments()[5];
    resolvable.getArguments()[5] = null;
    return getMessageSourceForCurrentModule(bundleKey).getMessage(
      resolvable,
      locale
    );
  }
}

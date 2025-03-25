package org.apache.struts.validator;

import static springing.util.ObjectUtils.parseConfigFileAt;

import javax.servlet.ServletException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.springframework.lang.Nullable;

public class ValidatorPlugIn implements PlugIn {

  public static final String PACKAGE_NAME = "org.apache.commons.validator";

  /**
   * Application scope key that `ValidatorResources` is stored under.
   */
  public static final String VALIDATOR_KEY =
    PACKAGE_NAME + ".VALIDATOR_RESOURCES";

  /**
   * Application scope key that `StopOnError` is stored under.
   */
  public static final String STOP_ON_ERROR_KEY =
    PACKAGE_NAME + ".STOP_ON_ERROR";

  @Override
  public void destroy() {
    // NOP
  }

  @Override
  public void init(ActionServlet servlet, ModuleConfig config)
    throws ServletException {
    if (pathnames == null) throw new IllegalArgumentException(
      "Pathname is required for the ValidatorPlugin of the module: " +
      config.getPrefix()
    );
    var paths = pathnames.split("\\s*,\\s*");
    for (var path : paths) {
      var resources = parseConfigFileAt(path, ValidatorResources.class);
      validatorResources.merge(resources);
    }
  }

  public ValidatorResources getValidatorResources() {
    return validatorResources;
  }

  private final ValidatorResources validatorResources =
    new ValidatorResources();

  public void setPathnames(String pathnames) {
    this.pathnames = pathnames;
  }

  private @Nullable String pathnames;

  public void setStopOnFirstError(boolean stopOnFirstError) {
    this.stopOnFirstError = stopOnFirstError;
  }

  private boolean stopOnFirstError = false;
}

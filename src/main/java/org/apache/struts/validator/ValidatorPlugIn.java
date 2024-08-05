package org.apache.struts.validator;

import jakarta.servlet.ServletException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.springframework.lang.Nullable;

import static springing.util.ObjectUtils.parseConfigFileAt;
public class ValidatorPlugIn implements PlugIn {

  /**
   * Application scope key that `ValidatorResources` is stored under.
   */
  public static final String VALIDATOR_KEY = "org.apache.commons.validator.VALIDATOR_RESOURCES";

  @Override
  public void destroy() {

  }

  @Override
  public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
    if (pathnames == null) throw new IllegalArgumentException(
      "Pathname is required for the ValidatorPlugin of the module: " + config.getPrefix()
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

  private final ValidatorResources validatorResources = new ValidatorResources();

  public void setPathnames(String pathnames) {
    this.pathnames = pathnames;
  }
  private @Nullable String pathnames;

  public void setStopOnFirstError(boolean stopOnFirstError) {
    this.stopOnFirstError = stopOnFirstError;
  }
  private boolean stopOnFirstError = false;
}

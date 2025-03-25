package org.apache.struts.plugins;

import javax.servlet.ServletException;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/**
 * Convenient implementation of {@link PlugIn} that performs as many
 * verification tests on the information stored in the {@link ModuleConfig}
 * for this module as is practical. Based on the setting of the `fatal`
 * property (which defaults to `true`), the detection of any such errors will
 * cause a `ServletException` to be thrown from the init method, which will
 * ultimately cause the initialization of your Struts controller servlet to
 * fail.
 */
public class ModuleConfigVerifier implements PlugIn {

  @Override
  public void destroy() {}

  @Override
  public void init(ActionServlet servlet, ModuleConfig config)
    throws ServletException {}
}

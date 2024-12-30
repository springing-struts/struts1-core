package org.apache.struts.action;

import javax.servlet.ServletException;
import org.apache.struts.config.ModuleConfig;

/**
 * A `PlugIn` is a configuration wrapper for a module-specific resource or
 * service that needs to be notified about application startup and application
 * shutdown events (corresponding to when the container calls `init` and
 * `destroy` on the corresponding `ActionServlet` instance). `PlugIn` objects
 * can be configured in the `struts-config.xml` file, without the need to
 * subclass `ActionServlet` simply to perform application lifecycle activities.
 * Implementations of this interface must supply a zero-argument constructor
 * for use by `ActionServlet`. Configuration can be accomplished by providing
 * standard JavaBeans property setter methods, which will all have been called
 * before the `init()` method is invoked.
 * This interface can be applied to any class, including an Action subclass.
 */
public interface PlugIn {
  /**
   * Receive notification that our owning module is being shut down.
   */
  void destroy();

  /**
   * Receive notification that the specified module is being started up.
   */
  void init(ActionServlet servlet, ModuleConfig config) throws ServletException;
}

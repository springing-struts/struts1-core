package org.apache.struts.action;

import org.apache.struts.config.ForwardConfig;

/**
 *  An `ActionForward` represents a destination to which the controller,
 *  `RequestProcessor`, might be directed to perform a
 *  `RequestDispatcher.forward()` or `HttpServletResponse.sendRedirect()` to,
 *  as a result of processing activities of an Action class. Instances of this
 *  class may be created dynamically as necessary, or configured in
 *  association with an ActionMapping instance for named lookup of potentially
 *  multiple destinations for a particular mapping instance.
 *  An ActionForward has the following minimal set of properties. Additional
 *  properties can be provided as needed by subclasses.
 *  **contextRelative**
 *    Should the path value be interpreted as context-relative (instead of
 *    module-relative, if it starts with a '/' character? [false]
 *  **name**
 *    Logical name by which this instance may be looked up in relationship to
 *    a particular ActionMapping.
 *  **path**
 *    Module-relative or context-relative URI to which control should be
 *    forwarded, or an absolute or relative URI to which control should be
 *    redirected.
 *  **redirect**
 *    Set to true if the controller servlet should call
 *    `HttpServletResponse.sendRedirect()` on the associated path; otherwise
 *    false.
 *  **NOTE**
 *  This class would have been deprecated and replaced by
 *  `org.apache.struts.config.ForwardConfig` except for the fact that it is
 *  part of the public API that existing applications are using.
 */
public class ActionForward extends ForwardConfig {

  public ActionForward() {
    this("");
  }

  public ActionForward(String path) {
    super("", path);
  }
}


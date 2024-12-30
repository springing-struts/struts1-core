package org.apache.struts.action;

import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.springframework.lang.Nullable;

/**
 * An `ActionMapping` represents the information that the controller,
 * `RequestProcessor`, knows about the mapping of a particular request to an
 * instance of a particular `Action` class. The `ActionMapping` instance used
 * to select a particular `Action` is passed on to that `Action`, thereby
 * providing access to any custom configuration information included with the
 * `ActionMapping` object.
 * **NOTE**
 *   This class would have been deprecated and replaced by
 *   `org.apache.struts.config.ActionConfig` except for the fact that it is
 *   part of the public API that existing applications are using.
 */
public class ActionMapping extends ActionConfig {

  protected ActionMapping() {
  }

  public ActionMapping(String path) {
    setPath(path);
  }

 /**
   * Find and return the `ForwardConfig` instance defining how forwarding to
   * the specified logical name should be handled. This is performed by
   * checking local and then global configurations for the specified forwarding
   * configuration. If no forwarding configuration can be found, return `null`.
   */
  public @Nullable ActionForward findForward(String forwardName) {
    return (ActionForward) findForwardConfig(forwardName);
  }

  /**
   * Find and return the `ForwardConfig` instance of mapping, throwing an
   * exception if not found locally or globally.
   */
  public ActionForward findRequiredForward(String forwardName) {
    var forward = findForward(forwardName);
    if (forward == null) throw new IllegalStateException(String.format(
      "Failed to find the forward [%s] for the action [%s].", forwardName, getPath()
    ));
    return forward;
  }

  /**
   * Return the logical names of all locally defined forwards for this mapping.
   * If there are no such forwards, a zero-length array is returned.
   */
  public String[] findForwards() {
    return getLocalForwards()
      .stream()
      .map(ForwardConfig::getName)
      .toList()
      .toArray(new String[]{});
  }

  /**
   * Create (if necessary) and return an `ActionForward` that corresponds to
   * the `input` property of this Action.
   * If the `input` property is not specified and the Controller is configured
   * to interpret the property as a forward, return the forward named "input"
   * (if it exists) in this action mapping.
   */
  public @Nullable ActionForward getInputForward() {
    var input = getInput();
    if (input == null) return null;
    return findRequiredForward(input);
  }
}
